function getRandomColour() {
    return '#' + Math.floor(Math.random() * 16777215).toString(16);
}

function colorSet(index, maxIndex) {
    var center = 128;
    var width = 127;
    var frequency = 2.4; //Math.PI * 2 / maxIndex;
    var red = Math.sin(frequency * index + 2) * width + center;
    var green = Math.sin(frequency * index + 0) * width + center;
    var blue = Math.sin(frequency * index + 4) * width + center;
    return RGB2Color(red, green, blue);
}

function RGB2Color(r, g, b) {
    return '#' + byte2Hex(r) + byte2Hex(g) + byte2Hex(b);
    //'#'+Math.floor(Math.random()*16777215).toString(16);
}

function byte2Hex(n) {
    var nybHexString = "0123456789ABCDEF";
    return String(nybHexString.substr((n >> 4) & 0x0F, 1)) + nybHexString.substr(n & 0x0F, 1);
}

function finder(cmp, arr, attr) {
    var val = arr[0][attr];
    for (var i = 1; i < arr.length; i++) {
        val = cmp(val, arr[i][attr]);
    }
    return val;
}

function pointInPolygon(ploygonArray, lat, lng) {

    var polySides = ploygonArray.length;

    var i, j = polySides - 1;
    var oddNodes = false;
    var polyLat;
    var polyLng;

    //0 is lng, 1 is lat
    for (i = 0; i < polySides; i++) {
        polyLat = ploygonArray[i][0];
        polyLng = ploygonArray[i][1];

        if ((ploygonArray[i][1] < lat && ploygonArray[j][1] >= lat
                || ploygonArray[j][1] < lat && ploygonArray[i][1] >= lat)
                && (ploygonArray[i][0] <= lng || ploygonArray[j][0] <= lng)) {
            if (ploygonArray[i][0] + (lat - ploygonArray[i][1]) / (ploygonArray[j][1] - ploygonArray[i][1]) * (ploygonArray[j][0] - ploygonArray[i][0]) < lng) {
                oddNodes = !oddNodes;
            }
        }
        j = i;
    }
    return oddNodes;
}