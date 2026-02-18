(function() {
    var toggleChevron = function(target, remove, add) {
        $(target).prev().find("[data-toggle='collapse'] .glyphicon")
                .removeClass('glyphicon-chevron-' + remove)
                .addClass('glyphicon-chevron-' + add);
    };

    $(".collapse").on('show.bs.collapse', function (e) {
        toggleChevron(e.target, 'right', 'down');
    }).on('hide.bs.collapse', function (e) {
        toggleChevron(e.target, 'down', 'right');
    });
})();
$(function () {
    $('[data-toggle="tooltip"]').tooltip();
});
