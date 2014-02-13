var map;

var jsonArray = [];
var boundaryArray = [];

var googleLayerSatellite;
var googleLayerStreet;
var openStreeMapLayer;

var markersCluster;

var transactedPriceHeatMap;

var PointSymbolMap;
var PointSymbolMapLegend;

var mrtMapLines = [];
var mrtMapLayerReference;

var polygonBoundary;
var polygonBoundaryLegend;

var proportionalSymbolMap;

var info;
var choroplethInfo;
var choroplethMaxValue;
var choroplethMinValue;
var numberOfChoroplethClasses = 9;

function loadScript() {

    $.ajaxSetup({async: false});

    map = new L.Map('map', {
        center: new L.LatLng(1.355312, 103.827068),
        zoom: 12
    });
    openStreeMapLayer = L.tileLayer.grayscale('http://{s}.tile.osm.org/{z}/{x}/{y}.png',
            {
                attribution: '&copy; <a href="http://osm.org/copyright">OpenStreetMap</a> contributors'
            });
    googleLayerSatellite = new L.Google('SATELLITE');
    googleLayerStreet = new L.Google('ROADMAP');
    var baseMaps = {
        'Google (Satellite)': googleLayerSatellite,
        'Google(Street)': googleLayerStreet,
        'Open Street Maps': openStreeMapLayer
    };
    markersCluster = L.markerClusterGroup();
    transactedPriceHeatMap = new L.TileLayer.HeatCanvas({}, {
        'step': 0.3,
        'degree': HeatCanvas.QUAD,
        'opacity': 0.5
    });
    PointSymbolMap = L.layerGroup();
    proportionalSymbolMap = L.layerGroup();

    $.getJSON('data/realis.json', function(data) {
        jsonArray = data;
        addMarkerCluster(jsonArray);
        addHeatMapLayer(jsonArray);
        addPointSymbolMap(jsonArray, 'propertyType');
        addProportionalSymbolMap(jsonArray, 'trasactedPrice');
        $.getJSON('data/Polygon.geojson', function(polygonData) {
            boundaryArray = polygonData;

            /* IMPORTANT!!
             //to abstract conbination of both arrays into a separate Method!
             */
            setChoroplethLayer(boundaryArray, jsonArray);
            //addProportionateSymbolMap(boundaryArray);
        });

    });

    /*
     $.getJSON('loadmrt', function(data) {
     jsonArray = data;
     addMRTLineMap(jsonArray);
     });
     */

    /*
     $.getJSON('data/sgmrtstations.geojson', function(data) {
     var myLayer = L.geoJson().addTo(map);
     myLayer.addData(data);
     });
     */

    mrtMapLayerReference = new L.geoJson();
    $.getJSON('data/sgrailnetwork.geojson', function(data) {
        mrtMapLayerReference.addData(data);
        mrtMapLayerReference.setStyle({color: '#de2d26'});
    });
    /*
     $.getJSON('data/sgroadsnetwork.geojson', function(data) {
     var myLayer = L.geoJson().addTo(map);
     myLayer.addData(data);
     });
     */

    var overlayMaps = {
        'Cluster Marker': markersCluster,
        'Point Symbol': PointSymbolMap,
        'Trasacted Price Heat Map': transactedPriceHeatMap,
        'MRT Map': mrtMapLayerReference,
        'Singapore Sub Zones': polygonBoundary,
        'Proportional Symbol (Transacted Price)': proportionalSymbolMap
    };
    map.addLayer(openStreeMapLayer);
    addHoverInfoControl();
    L.control.layers(baseMaps, overlayMaps, {collapsed: false}).addTo(map);
    addLayerChangeEventHandler();

    $.ajaxSetup({async: false});
    console.log("done");
    $(".loading").remove();

}

function addLayerChangeEventHandler() {
    map.on('overlayadd', function(eventLayer) {

        if (eventLayer.name === 'Point Symbol') {
            this.addControl(PointSymbolMapLegend);
            this.addControl(info);
        }

        if (eventLayer.name === 'Singapore Sub Zones') {
            this.addControl(polygonBoundaryLegend);
            this.addControl(choroplethInfo);
        }

    });
    map.on('overlayremove', function(eventLayer) {
        if (eventLayer.name === 'Point Symbol') {
            this.removeControl(PointSymbolMapLegend);
            this.removeControl(info);
        }

        if (eventLayer.name === 'Singapore Sub Zones') {
            this.removeControl(polygonBoundaryLegend);
            this.removeControl(choroplethInfo);
        }
    });
}


/*
 function addProportionateSymbolMap(polygonArray) {
 var circleParam = [];
 var numTransactions;
 var coordinates = [];
 
 for (var i in polygonArray['features']) {
 var polygon = [];
 
 if (polygonArray['features'][i]['geometry']["type"] === "MultiPolygon") {
 
 for (var k in polygonArray['features'][i]['geometry']['coordinates']) {
 for(var l in polygonArray['features'][i]['geometry']['coordinates'][k]){
 
 
 polygon.push(polygonArray['features'][i]['geometry']['coordinates'][k][l]);
 }
 }
 } else {
 polygon = polygonArray['features'][i]['geometry']['coordinates'][0];
 }
 
 
 numTransactions = polygonArray['features'][i]['transactions'].length;
 coordinates = calculateCentroid(polygon);
 circleParam.push({
 numTransactions: numTransactions,
 coordinates: coordinates
 });
 }
 
 //console.log(circleParam);
 
 }
 
 function calculateCentroid(polygon) {
 
 //console.log(polygon);
 
 var totalLat;
 var totalLng;
 
 for (var i in polygon) {
 totalLat += polygon[i][1];
 totalLng += polygon[i][0];
 }
 
 var lat = totalLat / polygon.length;
 var lng = totalLng / polygon.length;
 
 var coordinates = [lat, lng];
 
 //console.log(coordinates);
 
 return coordinates;
 }
 */

function addProportionalSymbolMap(json, categoryType) {
    //to modifyvar categoryTypeArray = [];

    var minValue = finder(Math.min, json, categoryType);
    $.each(json, function(index, transaction) {
        var circle = L.circle([transaction.lat, transaction.lng], transaction[categoryType] / minValue * 5, {
            color: 'black',
            weight: 1,
            fillColor: 'red',
            fillOpacity: 1,
            opacity: 1
        });
        circle.addTo(proportionalSymbolMap);
    });
}


function setChoroplethLayer(polygonArray, transactionArray) {
    var lat;
    var lng;

    for (var i in polygonArray['features']) {

        var coordArray = [];

        if (polygonArray['features'][i]['geometry']["type"] === "MultiPolygon") {
            for (var k in polygonArray['features'][i]['geometry']['coordinates']) {
                for (var l in polygonArray['features'][i]['geometry']['coordinates'][k]) {
                    coordArray.push(polygonArray['features'][i]['geometry']['coordinates'][k][l]);
                }
            }
        } else {
            coordArray = polygonArray['features'][i]['geometry']['coordinates'][0];
        }

        polygonArray['features'][i].transactions = [];
        for (var j in transactionArray) {
            lat = transactionArray[j].lat;
            lng = transactionArray[j].lng;
            if (pointInPolygon(coordArray, lat, lng)) {
                polygonArray['features'][i].transactions.push(transactionArray[j]);
            }
        }
    }

    var numberTransaction = [];
    for (var i  in polygonArray['features']) {
        numberTransaction.push(polygonArray['features'][i].transactions.length);
    }
    choroplethMaxValue = Math.max.apply(Math, numberTransaction);
    choroplethMinValue = Math.min.apply(Math, numberTransaction);

    polygonBoundary = new L.geoJson(polygonArray, {style: styleChoropleth, onEachFeature: onChoroplethEachFeature});

    setChoroplethLegend();
    addChoroplethHoverInfoControl();
}

function onChoroplethEachFeature(feature, layer) {
    layer.on({
        mouseover: mouseoverChoroplethLayer,
        mouseout: mouseoutChoroplethLayer,
        click: zoomToFeature
    });
}

function mouseoverChoroplethLayer(e) {
    var layer = e.target;
    layer.setStyle({
        color: 'black'
    });
    choroplethInfo.update(layer.feature);
}

function mouseoutChoroplethLayer(e) {
    var layer = e.target;
    layer.setStyle({
        color: 'white'
    });
    choroplethInfo.update();
}

function addChoroplethHoverInfoControl() {
    choroplethInfo = L.control({position: 'bottomleft'});
    choroplethInfo.onAdd = function(map) {
        this._div = L.DomUtil.create('div', 'info');
        this.update();
        return this._div;
    };
    choroplethInfo.update = function(subzone) {
        this._div.innerHTML = '<h4>Sub Zone</h4>' + (subzone ? getSubZoneInfo(subzone) : 'Hover over a Sub Zone');
    };
}

function getSubZoneInfo(subzone) {
    var zone = subzone.properties;
    var transactionCount = subzone.transactions.length;

    return '<b>Area Name: ' + zone.DGPZ_NAME + '</b><br />' +
            'Sub Area Name: ' + zone.DGPSZ_NAME + '<br>' +
            'Number of Transactions: ' + transactionCount + '<br>';
}

function setChoroplethLegend() {
    var colourArray = [];
    var interval = (choroplethMaxValue - choroplethMinValue) / numberOfChoroplethClasses;
    var lowerBound = choroplethMinValue;
    var upperBound = choroplethMinValue + interval;
    var key;

    while (upperBound < choroplethMaxValue) {
        key = Math.round(lowerBound) + "-" + Math.round(upperBound);
        colourArray[key] = getChoroplethColour(upperBound);
        lowerBound = upperBound;
        upperBound = upperBound + interval;
    }

    polygonBoundaryLegend = getLegend(colourArray, 'Number of Transactions');
}

function styleChoropleth(feature) {
    return {
        fillColor: getChoroplethColour(feature.transactions.length),
        weight: 2,
        opacity: 1,
        color: 'white',
        dashArray: '3',
        fillOpacity: 0.7
    };
}

function getChoroplethColour(value) {
    var interval = (choroplethMaxValue - choroplethMinValue) / numberOfChoroplethClasses;

    return value > choroplethMaxValue - 1 * interval ? '#08306b' :
            value > choroplethMaxValue - 2 * interval ? '#08519c' :
            value > choroplethMaxValue - 3 * interval ? '#2171b5' :
            value > choroplethMaxValue - 4 * interval ? '#4292c6' :
            value > choroplethMaxValue - 5 * interval ? '#6baed6' :
            value > choroplethMaxValue - 6 * interval ? '#9ecae1' :
            value > choroplethMaxValue - 7 * interval ? '#c6dbef' :
            value > choroplethMaxValue - 8 * interval ? '#deebf7' :
            '#f7fbff';
}

//method obsolete
function addMRTLineMap(json) {
    var MRTLineArray = [];
    $.each(json, function(index, mrtStation) {
        if ($.inArray(mrtStation.lineCode, MRTLineArray) === -1) {
            MRTLineArray[mrtStation.lineCode] = new Array();
        }
    });
    $.each(json, function(index, mrtStation) {
        MRTLineArray[mrtStation.lineCode].push(mrtStation);
    });
    //hard-coded to remove lines not in operation
    delete MRTLineArray["BP"];
    delete MRTLineArray["CE"];
    delete MRTLineArray["CG"];
    delete MRTLineArray["TS"];
    var point;
    for (var stationLine in MRTLineArray) {
        var pointList = [];
        for (var station in MRTLineArray[stationLine]) {
            point = new L.LatLng(MRTLineArray[stationLine][station].lat, MRTLineArray[stationLine][station].lng);
            pointList.push(point);
        }

        var color = 'red';
        var polyline = new L.Polyline(pointList, {color: color});
        mrtMapLines.push(polyline);
    }
}

//Information Control for dynamic tooltipping
function getTransactionInfo(transaction) {
    return '<b>Project Name: ' + transaction.projectName + '</b><br />' +
            'Address: ' + transaction.address + '<br>' +
            'Postal Code: ' + transaction.postalCode + '<br>' +
            'Type of Area: ' + transaction.typeOfArea + '<br>' +
            'Trasacted Price: $' + transaction.trasactedPrice + '<br>' +
            'Area (m<sup>2</sup>): ' + transaction.areaSQM + '<br>' +
            'Unit Price (per m<sup>2</sup>): $' + transaction.unitPricePSM + '<br>' +
            'Property Type: ' + transaction.propertyType + '<br>' +
            'Type of Sale: ' + transaction.typeOfSale + '<br>' +
            'Planning Region: ' + transaction.planningRegion + '<br>' +
            'Planning Area: ' + transaction.planningArea + '<br>';
}

function addHoverInfoControl() {
    info = L.control({position: 'bottomleft'});
    info.onAdd = function(map) {
        this._div = L.DomUtil.create('div', 'info');
        this.update();
        return this._div;
    };
    info.update = function(transaction) {
        this._div.innerHTML = '<h4>Property Transaction</h4>' + (transaction ? getTransactionInfo(transaction) : 'Hover over a transaction');
    };
}

function addPointSymbolMap(json, categoryType) {

    var categoryTypeArray = [];
    $.each(json, function(index, transaction) {
        if ($.inArray(transaction[categoryType], categoryTypeArray) === -1) {
            categoryTypeArray.push(transaction[categoryType]);
        }
    });
    var colourArray = [];
    $.each(categoryTypeArray, function(index, categoryType) {
        colourArray[categoryType] = colorSet(index, categoryTypeArray.length);
        //colourArray[propertyType] = getRandomColour();
    });
    //var minTrasactedPrice = finder(Math.min, json, "trasactedPrice");
    $.each(json, function(index, transaction) {
        var circle = L.circleMarker(L.latLng(transaction.lat, transaction.lng), {
            radius: 5,
            color: 'black',
            weight: 1,
            fillColor: colourArray[transaction[categoryType]],
            fillOpacity: 1,
            opacity: 1
        });
        circle.on('mouseover', onCircleMouseOver);
        circle.on('mouseout', onCircleMouseOut);
        circle.on('click', zoomToFeature);
        circle.bindPopup(getTransactionInfo(transaction));
        circle.transaction = transaction;
        circle.addTo(PointSymbolMap);
    });
    PointSymbolMapLegend = getLegend(colourArray, 'Proportion Legend');
}

function addHeatMapLayer(json) {
    /*
     transactedPriceHeatMap.onRenderingStart(function() {
     console.log('rendering');
     });
     transactedPriceHeatMap.onRenderingEnd(function() {
     alert('rendered');
     });
     */
    var minTrasactedPrice = finder(Math.min, json, "trasactedPrice");
    $.each(json, function(index, transaction) {
        transactedPriceHeatMap.pushData(transaction.lat, transaction.lng, (transaction.trasactedPrice / minTrasactedPrice));
    });
}

function addMarkerCluster(json) {
    //to modify
    $.each(json, function(index, transaction) {
        var marker = L.marker(new L.LatLng(transaction.lat, transaction.lng), {
        });
        marker.bindPopup(getTransactionInfo(transaction));
        markersCluster.addLayer(marker);
    });
}

function getLegend(colourArray, legendHeading) {
    var legend = L.control({position: 'bottomright'});
    legend.onAdd = function(map) {
        var div = L.DomUtil.create('div', 'info legend');
        var legendInput = '<h4>' + legendHeading + '</h4>';
        for (var k in colourArray) {
            legendInput += '<i style="background:' + colourArray[k] + '"></i> ' + k + '<br>';
        }

        div.innerHTML = legendInput;
        return div;
    };
    return legend;
}

function onCircleMouseOver(e) {
    var layer = e.target;
    layer.setStyle({
        weight: 5
    });
    info.update(layer.transaction);
    //layer.setRadius(100);
}

function onCircleMouseOut(e) {
    var layer = e.target;
    layer.setStyle({
        weight: 3
    });
    info.update();
    //layer.setRadius(20);
}

function zoomToFeature(e) {
    map.fitBounds(e.target.getBounds());
}
