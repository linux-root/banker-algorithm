App.factory('ApiQuery', function ($http, ApiUrl) {
    return {
        get: function (url, data) {
            var param = jQuery.param(data);
            return $http.get(ApiUrl + url + '?' + param);
        },
        post: function (url, data) {
            return $http({
                method: 'POST',
                url: ApiUrl + url,
                headers: {
                    'Content-Type': undefined
                },
                data: {
                    data: data
                }
            });
        },
        put: function (url, data) {
            return $http({
                method: 'PUT',
                url: ApiUrl + url,
                headers: {
                    'Content-Type': undefined
                },
                data: {
                    data: data
                }
            });
        },
        request: function (type, url, data) {
            return $http({
                method: type.toUpperCase(),
                url: ApiUrl + url,
                headers: {
                    'Content-Type': undefined
                },
                data: {
                    data: data
                }
            });
        },
        delete: function (url, data) {
            return $http.delete(ApiUrl + url, data);
        }
    }
});
