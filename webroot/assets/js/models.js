
/*
 * Create model with Update method changed to PUT request
 */
App.factory('DemoModel', ['$resource', 'ApiUrl', function ($resource, ApiUrl) {
    return $resource(ApiUrl + 'models/:database_id.json', {database_id: '@database_id'}, {
        'get': {
            method: 'GET',
            transformResponse: function (response) {
                response = JSON.parse(response);
                return response.data;
            }
        },
        'query': {
            method: 'GET',
            isArray: true,
            transformResponse: function (response) {
                response = JSON.parse(response);
                return response.data;
            }
        },
        'save': {
            method: 'POST',
            transformRequest: function (request) {
                request.number = parseInt(request.number);
                return JSON.stringify({data: request});
            },
            transformResponse: function (response) {
                response = JSON.parse(response);
                return response.data;
            }
        },
        'update': {
            method: 'PUT',
            transformRequest: function (request) {
                request.number = parseInt(request.number);
                return JSON.stringify({data: request});
            },
            transformResponse: function (response) {
                response = JSON.parse(response);
                return response.data;
            }
        },
        'delete': {
            method: 'DELETE',
            transformResponse: function (response) {
                response = JSON.parse(response);
                return response.data;
            }
        }
    });
}]);
