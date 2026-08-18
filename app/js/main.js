// Script para el efecto de header "glass"
// Al hacer scroll (window.scrollY > 50) se agrega la clase .scrolled al header,
// que activa el fondo translúcido, el blur y el borde inferior definidos en <style>.
// Si el usuario vuelve arriba del todo, la clase se elimina y el header regresa a su estado base.
window.addEventListener('scroll', () => {
        const header = document.getElementById('main-header');
        if (window.scrollY > 50) {
                header.classList.add('scrolled');
        } else {
                header.classList.remove('scrolled');
        }
});
