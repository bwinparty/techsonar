function techRadar() {
    var self;
    self = this;
    this.send = function(object, clazz, type) {
        var data, consolediv;
        consolediv = $('#console');
        data = JSON.stringify(object);
        $.ajax({
            url: 'rest/' + clazz,
            type: type,
            data: data,
            dataType: 'json',
            success: function(response) {
                if (response.message) {
                    consolediv.text(response.message + (response.id === undefined ? "" : " " + response.id)).show().delay(1000).fadeOut("slow");
                } else {
                    consolediv.text(response.error).show().delay(1000).fadeOut("slow");
                }
            }
        });
    };
    this.get = function(clazz) {
        var consolediv;
        consolediv = $('#console');
        $.ajax({
            url: 'rest/' + clazz,
            type: 'GET',
            success: function(response) {
                consolediv.text("");
                switch(clazz)
                {
                    case "items":
                        $.each(response, function(key, value) {
                            consolediv.append(value.id + " " + value.name + "<br>");
                        });
                        consolediv.show().delay(1000).fadeOut("slow");
                    break;
                    case "tracks":
                    case "sectors":
                        $.each(response, function(key, value) {
                            consolediv.append(value.id + " " + value.name + "<br>");
                        });
                        consolediv.show().delay(1000).fadeOut("slow");
                    break;
                }
            }
        });
    };
    this.init = function() {
        $('#getitems').click(function() {
            self.get("items");
        });
        $('#insertitem').click(function() {
            var item, version;
            item = new Object();
            item.shortcut = "testweb";
            item.name = "fromtestweb";
            item.description = "fromtestweb";
            item.coordinates = new Object();
            item.coordinates.angular = 5;
            item.coordinates.radial = 10;
            version = new Object();
            version.id = "test";
            version.details = "details";
            version.state = "APPROVED";
            item.versions = new Array();
            item.versions.push(version);
            item.sector_id = "9d312b9f-24e7-4522-a226-2d97310fdc10";
            item.track_id = "9d312b9f-24e7-4522-a226-2d97310fdc11";
            self.send(item, "items", "POST");
        });
        $('#updateitem').click(function() {
            var item, id;
            id = prompt("Please enter your the item id");
            if (id !== null && id !== "") {
                item = new Object();
                item.id = id;
                item.shortcut = "testweb";
                item.name = "fromtestweb1234";
                item.coordinates = new Object();
                item.coordinates.angular = 6;
                item.coordinates.radial = 1;
                self.send(item, "items", "PUT");
            }
        });
        $('#deleteitem').click(function() {
            var item, id;
            id = prompt("Please enter your the item id");
            if (id !== null && id !== "") {
                item = new Object();
                item.id = id;
                self.send(item, "items", "DELETE");
            }
        });
        $('#gettracks').click(function() {
            self.get("tracks");
        });
        $('#inserttrack').click(function() {
            var track;
            track = new Object();
            track.name = "Adopt";
            track.description = "Adopt";
            track.tags = ["application","techradar"];
            track.weight = 1;
            self.send(track, "tracks", "POST");
        });
        $('#updatetrack').click(function() {
            var track, id;
            id = prompt("Please enter your the track id");
            if (id !== null && id !== "") {
                track = new Object();
                track.id = id;
                track.name = "Adopt";
                track.description = "Adopt modified";
                track.weight = 2;
                self.send(track, "tracks", "PUT");
            }
        });
        $('#deletetrack').click(function() {
            var track, id;
            id = prompt("Please enter your the track id");
            if (id !== null && id !== "") {
                track = new Object();
                track.id = id;
                self.send(track, "tracks", "DELETE");
            }
        });
        $('#getsectors').click(function() {
            self.get("sectors");
        });
        $('#insertsector').click(function() {
            var sector;
            sector = new Object();
            sector.name = "Techniques";
            sector.description = "Techniques";
            sector.tags = ["application","techradar"];
            sector.weight = 1;
            self.send(sector, "sectors", "POST");
        });
        $('#updatesector').click(function() {
            var sector, id;
            id = prompt("Please enter your the sector id");
            if (id !== null && id !== "") {
                sector = new Object();
                sector.id = id;
                sector.name = "Techniques";
                sector.description = "Techniques modified";
                sector.weight = 2;
                self.send(sector, "sectors", "PUT");
            }
        });
        $('#deletesector').click(function() {
            var sector, id;
            id = prompt("Please enter your the sector id");
            if (id !== null && id !== "") {
                sector = new Object();
                sector.id = id;
                self.send(sector, "sectors", "DELETE");
            }
        });
    };
}

$(function() {
    var TR;
    TR = new techRadar();
    TR.init();
});