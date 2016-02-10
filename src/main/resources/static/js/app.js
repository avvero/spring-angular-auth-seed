angular.module("app", [
    'ngRoute',
    'ui.router',
    'ngSanitize'
])
angular.module("app").constant('constants', {
    version: "1.0.0"
})
angular.module("app").constant('AUTH_EVENTS', {
    loginSuccess: 'auth-login-success',
    loginFailed: 'auth-login-failed',
    logoutSuccess: 'auth-logout-success',
    sessionTimeout: 'auth-session-timeout',
    notAuthenticated: 'auth-not-authenticated',
    notAuthorized: 'auth-not-authorized'
})
angular.module("app").constant('USER_ROLES', {
    ANONYMOUS: 'ANONYMOUS',
    ALL: '*',
    USER: 'USER'
})
// configure our routes
angular.module("app").config(function ($routeProvider, $stateProvider, $urlRouterProvider, $httpProvider,
                                       USER_ROLES) {

    $urlRouterProvider.otherwise("/")

    $stateProvider
        .state('welcome', {
            url: "/",
            views: {
                "single": {
                    templateUrl: 'view/welcome.html',
                    controller: welcomeController
                }
            },
            data: {
                authorizedRoles: [USER_ROLES.ANONYMOUS]
            }
        })

        .state('authed', {
            url: "/authed",
            views: {
                "single": {
                    templateUrl: 'view/welcome.html',
                    controller: welcomeController
                }
            },
            data: {
                authorizedRoles: [USER_ROLES.USER]
            }
        })

    $httpProvider.interceptors.push([
        '$injector',
        function ($injector) {
            return $injector.get('AuthInterceptor');
        }
    ]);

})
angular.module("app").run(function ($rootScope, $http, AuthService, AUTH_EVENTS, USER_ROLES) {
    $rootScope.$on('$stateChangeStart', function (event, next) {
        var authorizedRoles = next.data.authorizedRoles;
        if (authorizedRoles.indexOf(USER_ROLES.ANONYMOUS) !== -1) {
            // allow
        } else {
            if (!AuthService.isAuthorized(authorizedRoles)) {
                event.preventDefault();
                if (AuthService.isAuthenticated()) {
                    // user is not allowed
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthorized);
                } else {
                    // user is not logged in
                    $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
                }
            }
        }
    });

    //$http({
    //    method: 'GET',
    //    url: 'profile',
    //    headers: {'Content-Type': 'application/json;charset=UTF-8'}
    //})
    //    .success(function (user) {
    //        Session.create(null, user.id, null);
    //    })
    //    .error(function (data) {
    //        $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
    //    });
})

angular.module("app").controller('mainController', function ($scope, page, $http, $window, AUTH_EVENTS) {
    page.setTitle("Indexy")

    $scope.auth = function() {
        $http({
            method: 'GET',
            url: 'oauth2/start?provider=vk',
            headers: {'Content-Type': 'application/json;charset=UTF-8'}
        })
            .success(function (pair) {
                var w = 605
                var h = 429
                var left = (screen.width/2)-(w/2);
                var top = (screen.height/2)-(h/2);
                $window.open(pair.value, "OAuth", 'toolbar=no, location=no, directories=no, status=no, menubar=no, scrollbars=no, resizable=no, copyhistory=no, width='+w+', height='+h+', top='+top+', left='+left);
            })
            .error(function (data) {

            });
    }
    $scope.$on(AUTH_EVENTS.notAuthenticated, $scope.auth);
    $scope.$on(AUTH_EVENTS.sessionTimeout, $scope.auth)

})