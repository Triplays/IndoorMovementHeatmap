nav = document.getElementsByTagName('nav')[0];
menu = document.getElementsByTagName('menu')[0];
main = document.getElementsByTagName('main')[0];

nav.addEventListener('click', function () {
    this.classList.add('hover');
});

menu.addEventListener('click', function () {
    this.classList.add('hover');
});

main.addEventListener('click', function () {
    nav.classList.remove('hover');
    menu.classList.remove('hover');
    app.test();
});

nav_items = nav.getElementsByClassName('nav_item');
for (let i = 0; i < nav_items.length; i++) {
    const item = nav_items[i];
    const name = item.innerHTML.replace(/\s/g, '');
    item.onclick = function () {
        menu.classList.add('hover');
        var xhttp = new XMLHttpRequest();
        xhttp.onreadystatechange = function () {
            if ((this.readyState === 4) && (this.status === 200 || this.status ===0)) {
                menu.innerHTML = this.responseText;
                load_options();
            } else if (this.status === 404) {
                menu.innerHTML = 'Error ' + this.status;
            }
        };
        xhttp.open("GET", 'menu/' + name + ".html", true);
        xhttp.send();
    }
}








