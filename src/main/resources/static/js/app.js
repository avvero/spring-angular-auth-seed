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
                    controller: welcomeController,
                    resolve: welcomeController.resolve
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
angular.module("app").run(function ($rootScope, $http, AuthService, AUTH_EVENTS, Session) {
    $rootScope.$on('$stateChangeStart', function (event, next) {
        var authorizedRoles = next.data.authorizedRoles;
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
    });

    $http({
        method: 'GET',
        url: 'profile',
        headers: {'Content-Type': 'application/json;charset=UTF-8'}
    })
        .success(function (user) {
            Session.create(null, user.id, null);
        })
        .error(function (data) {
            $rootScope.$broadcast(AUTH_EVENTS.notAuthenticated);
        });
})

angular.module("app").controller('mainController', function ($scope, page) {
    page.setTitle("Indexy")
})