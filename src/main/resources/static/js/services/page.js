angular.module('app').factory('page', function () {
    var title = 'app';
    return {
        title: function () {
            return title;
        },
        setTitle: function (newTitle) {
            title = newTitle
        }
    };
});