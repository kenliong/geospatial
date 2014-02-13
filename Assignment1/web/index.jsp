<!DOCTYPE html>
<html>
    <head>
        <meta name="viewport" content="initial-scale=1.0, user-scalable=no" />
        <!--main stylesheet-->
        <link rel="stylesheet" href="css/main.css"/>

        <!--leaflet stylesheet-->
        <link rel="stylesheet" href="css/leaflet.css"/>
        <link rel="stylesheet" href="css/MarkerCluster.css"/>
        <link rel="stylesheet" href="css/MarkerCluster.Default.css"/>
        <!--[if lte IE8]>
        <link rel="stylesheet" href=css/leaflet.ie.css"/>
        <![endif]-->
        <!--libraries-->
        <script src="js/leaflet.js"></script>
        <script src='js/jquery-1.11.0.js'></script>
        <script src="js/leaflet-google.js"></script>
        <script src="js/leaflet.markercluster.js"></script>
        <script src="js/TileLayer.Grayscale.js"></script>
        <script src="js/heatcanvas.js"></script>
        <script src="js/heatcanvas-leaflet.js"></script>
        <script src='https://maps.googleapis.com/maps/api/js?key=AIzaSyDbtDPnT0d7d8mKYnkua06UZzDgksNIo8A&sensor=true'></script>
        
        <!--link to main javascript file-->
        <script src="js/main.js"></script>
        <script src="js/utility.js"></script>
        <script>
            

            window.onload = loadScript;

        </script>
    </head>
    <body>
        <div class="loading"><img src="img/loading.gif"/></div>
        <div id="map"/>
    </body>
</html>