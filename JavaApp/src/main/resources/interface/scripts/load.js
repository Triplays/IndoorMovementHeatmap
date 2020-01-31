function load_date_picker() {
    var date_from = flatpickr('#date_from');
    var date_till = flatpickr('#date_till');
}

function load_sliders() {
    var sliders = document.getElementsByClassName("slider");
    var outputs = document.getElementsByClassName("slider_count");
    for (var i = 0; i < sliders.length; i++) {
        var output = outputs[i];
        var slider = sliders[i];
        output.innerHTML = slider.value; // Display the default slider value
        slider.output = output;
        // Update the current slider value (each time you drag the slider handle)
        slider.addEventListener('input', function () {
            this.output.innerHTML = this.value;
        });
    }


}

function set_slider(id) {
    var element = document.getElementById(id);
    if (element) {
        element.value = app.get(id);
        element.id_temp = id;
        element.addEventListener('input', function () {
            app.set(this.id_temp, this.value);
        });
    }
}

function set_select_menu(id) {
    var element = document.getElementById(id);
    if (!element) return;
    var json = JSON.parse(app.get(id));
    var select = element.getElementsByTagName("select")[0];
    if (!select) return;
    var holder = select.firstElementChild.cloneNode();
    select.innerHTML = "";
    // app.print(json);
    for (var i = 1; i < json.length; i++) {
        holder.value = json[i].toLowerCase();
        holder.innerHTML = json[i];
        select.innerHTML += holder.outerHTML;
    }
    element.addEventListener('click', function () {
        // app.print('click');
        // app.print(this.id.toString());
        var choice = this.getElementsByClassName('select-selected')[0].innerHTML;
        // app.print(choice.innerHTML.toLowerCase());
        app.set(this.id, choice.toLowerCase());
    });
    if (id === "object_type")
        element.addEventListener('click', function () {
            // const name = item.href;
            menu.classList.add('hover');
            var xhttp = new XMLHttpRequest();
            xhttp.onreadystatechange = function () {
                if ((this.readyState === 4) && (this.status === 200 || this.status === 0)) {
                    menu.innerHTML = this.responseText;
                    load_options();
                } else if (this.status === 404) {
                    menu.innerHTML = 'Error ' + this.status;
                }
            };
            xhttp.open("GET", 'menu/Device.html', true);
            xhttp.send();

        });
    select.value = json[0].toLowerCase();
}

function set_check_boxes(group) {
    var element = document.getElementById(group);
    if (!element) return;
    var holder = element.firstElementChild.cloneNode(true);
    element.group = group;

    element.addEventListener('click', function () {
        var inputs = this.getElementsByTagName('input');
        // app.print('click');
        for (var i = 1; i < inputs.length; i++) {
            if (inputs[i].checked === true) {
                app.append(this.group, inputs[i].value);
            } else {
                app.remove(this.group, inputs[i].value);
            }
        }
    });
    element.innerHTML = "";
    element.innerHTML += holder.outerHTML;

    var options = JSON.parse(app.get(group));
    var selected = JSON.parse(app.get(group + '_s')); // _s means selected

    for (var i = 0; i < options.length; i++) {
        holder.getElementsByTagName('div')[0].innerHTML = options[i];
        holder.getElementsByTagName('input')[0].value = options[i];
        element.innerHTML += holder.outerHTML;
    }

    var labels = element.getElementsByTagName('label');
    labels[0].addEventListener('click', function () {
        var inputs = this.parentElement.getElementsByTagName('input');
        for (var i = 1; i < inputs.length; i++) {
            if (inputs[i].checked !== inputs[0].checked)
                inputs[i].checked = inputs[0].checked;
        }
    });
    for (var i = 1; i < labels.length; i++) {
        labels[i].getElementsByTagName('input')[0].checked = selected.includes(options[i - 1]);
        // app.print(labels[i].getElementsByTagName('input')[0].checked);
        labels[i].addEventListener('click', function () {
            var input = this.getElementsByTagName('input')[0];
            var inputs = this.parentElement.getElementsByTagName('input');
            inputs[0].checked = input.checked;
            for (var i = 1; i < inputs.length || inputs[0].checked; i++) {
                inputs[0].checked &= inputs[i].checked;
            }
        });
    }
}

function set_radio_buttons(group) {
    var elements = document.getElementsByName(group);
    if (elements.length === 0) return;
    var checked = app.get(group);
    for (var i = 0; i < elements.length; i++) {
        elements[i].checked = elements[i].value === checked;
        elements[i].addEventListener('click', function () {
            app.set(this.name, this.value);
        })
    }
}


function load_heat_map() {
    var img = document.getElementById('heatmap');
    // app.print(app.get("image"));
    img.style.backgroundImage = "url('images/Zernike heatmap.png')";
    setTimeout(function () {
        var img = document.getElementById('heatmap');
        // app.print(app.get("image"));
        img.style.backgroundImage = "url('file:/" + app.get("image") + "')";
    }, 100);
}

function load_options() {
    try {
        set_slider('time_amount');
        set_slider('time_spent');
        set_slider('radius');
        set_select_menu('point_opacity');
        set_select_menu('color_mapping');
        set_select_menu('object_type');
        set_radio_buttons('time');
        set_radio_buttons('parameter');
        set_check_boxes('devices');
        load_select_menu();
        load_date_picker();
        load_sliders();
    } catch (e) {
        try {
            app.print(e);
        } catch {
            console.log(e);
        }
    }
}


function load_select_menu() {
    var x, i, j, selElmnt, a, b, c;
    /* Look for any elements with the class "custom-select": */
    x = document.getElementsByClassName("custom-select");
    for (i = 0; i < x.length; i++) {
        selElmnt = x[i].getElementsByTagName("select")[0];
        /* For each element, create a new DIV that will act as the selected item: */
        a = document.createElement("DIV");
        a.setAttribute("class", "select-selected");
        a.innerHTML = selElmnt.options[selElmnt.selectedIndex].innerHTML;
        x[i].appendChild(a);
        /* For each element, create a new DIV that will contain the option list: */
        b = document.createElement("DIV");
        b.setAttribute("class", "select-items select-hide");
        for (j = 0; j < selElmnt.length; j++) {
            /* For each option in the original select element,
            create a new DIV that will act as an option item: */
            c = document.createElement("DIV");
            c.innerHTML = selElmnt.options[j].innerHTML;
            c.addEventListener("click", function (e) {
                /* When an item is clicked, update the original select box,
                and the selected item: */
                var y, i, k, s, h;
                s = this.parentNode.parentNode.getElementsByTagName("select")[0];
                h = this.parentNode.previousSibling;
                for (i = 0; i < s.length; i++) {
                    if (s.options[i].innerHTML === this.innerHTML) {
                        s.selectedIndex = i;
                        h.innerHTML = this.innerHTML;
                        y = this.parentNode.getElementsByClassName("same-as-selected");
                        for (k = 0; k < y.length; k++) {
                            y[k].removeAttribute("class");
                        }
                        this.setAttribute("class", "same-as-selected");
                        break;
                    }
                }
                h.click();
            });
            b.appendChild(c);
        }
        x[i].appendChild(b);
        a.addEventListener("click", function (e) {
            /* When the select box is clicked, close any other select boxes,
            and open/close the current select box: */
            e.stopPropagation();
            closeAllSelect(this);
            this.nextSibling.classList.toggle("select-hide");
            this.classList.toggle("select-arrow-active");
        });
    }

    function closeAllSelect(elmnt) {
        /* A function that will close all select boxes in the document,
        except the current select box: */
        var x, y, i, arrNo = [];
        x = document.getElementsByClassName("select-items");
        y = document.getElementsByClassName("select-selected");
        for (i = 0; i < y.length; i++) {
            if (elmnt == y[i]) {
                arrNo.push(i)
            } else {
                y[i].classList.remove("select-arrow-active");
            }
        }
        for (i = 0; i < x.length; i++) {
            if (arrNo.indexOf(i)) {
                x[i].classList.add("select-hide");
            }
        }
    }

    /* If the user clicks anywhere outside the select box,
    then close all select boxes: */
    document.addEventListener("click", closeAllSelect);
}
