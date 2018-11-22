
function weather() {
    var location = document.getElementById("location");
    var apiKey = "f79391134e06a4e28c4972a3cafd6587";
    var url = "http://api.openweathermap.org/data/2.5/weather";

    navigator.geolocation.getCurrentPosition(success, error);

    function success(position) {
        latitude = position.coords.latitude;
        longitude = position.coords.longitude;

    }

    function error() {
        location.innerHTML = "Unable to retrieve your location";
    }

    //location.innerHTML = "Locating...";
}

weather();