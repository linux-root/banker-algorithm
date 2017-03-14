App.config(['$httpProvider', 'jwtOptionsProvider', function ($httpProvider, jwtOptionsProvider) {
    jwtOptionsProvider.config({
        tokenGetter: function () {
            var $theToken = window.localStorage.getItem('__the_token');
            if ($theToken != null && JSON.parse($theToken)) {
                $theToken = JSON.parse($theToken);
                return $theToken.access_token;
            } else {
                window.localStorage.removeItem('__the_token');
                return null;
            }
        },
        unauthenticatedRedirector: ['$state', function ($state) {
            $state.go('loginPage');
        }],
        whiteListedDomains: [
            'localhost',
            '127.0.0.1'
        ]
    });
    $httpProvider.interceptors.push('jwtInterceptor');
}]);

App.factory('TokenProvider', ['$http', '$rootScope', 'ApiUrl', function ($http, $rootScope, ApiUrl) {
    return {
        grant: function (gtoken) {
            return $http({
                method: 'POST',
                url: ApiUrl + 'authorization.json',
                skipAuthorization: true,
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                data: $.param({
                    gtoken: gtoken
                })
            });
        },
        refresh: function () {
            var $theToken = window.localStorage.getItem('__the_token');
            $theToken = JSON.parse($theToken);
            return $http({
                method: 'POST',
                url: RestSSO['auth-server-url'] + '/realms/' + RestSSO.realm + '/protocol/openid-connect/token',
                skipAuthorization: true,
                headers: {
                    'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'
                },
                data: $.param({
                    client_id: RestSSO.resource,
                    client_secret: RestSSO.credentials.secret,
                    grant_type: 'refresh_token',
                    refresh_token: $theToken.refresh_token
                })
            });
        }
    }
}]);