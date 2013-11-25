/*
 * techsonar - Visualization of Technologies Used
 * 
 * Copyright 2013   bwin.party digital entertainment plc
 *                  http://www.bwinparty.com
 * Developer: Manuel Bauer
 */


/// <reference path="lib/d3.v3.js" />

if (typeof (Number.prototype.toRad) === "undefined") {
    Number.prototype.toRad = function () {
        return this * Math.PI / 180;
    }
}

if (typeof Number.prototype.toDeg == 'undefined') {
    Number.prototype.toDeg = function () {
        return this * 180 / Math.PI;
    }
}


function techRadar() {

    //constants
    var NODESTATE;
    //fields
    var self, canvasSize, container, tracks, sectors, nodes, tooltip, details, toast,  editableFields;

    NODESTATE = new Object();
    NODESTATE.NEW = "NEW";
    NODESTATE.DELETED = "DELETED";
    NODESTATE.USED = "USED";

    self = this;
    canvasSize = 820;
    rootUrl = "/techsonar";
    editableFields = [ "name", "description", "vendor", "url", "license", "shortcut" ];

    this.init = function () {

        self.loadBootStrapData(function () {

            container = d3.select("#radar")
                .append("svg")
                .attr("width", canvasSize)
                .attr("height", canvasSize);

            tooltip = d3.select("body").append("div")
                .attr("class", "tooltip")
                .style("opacity", 0);

            toast = d3.select("body").append("div")
                .attr("class", "toast")
                .attr("id", "toast")
                .style("opacity", 0);


            details = d3.select("body").append("div")
                .attr("class", "details")
                .attr("id", "details")
                .style("opacity", 0);



            self.shadowDefs(container.append('svg:defs'));

            self.drawTracks();
            self.drawSectors();
            self.placeNodes();
            self.drawTrash();

        });
    };

    this.placeNodes = function () {

        var node, nodeEnter;

        node = container.selectAll("svg")
            .data(nodes);

        nodeEnter = node.enter().append("g")
			.attr("transform", function (d) {
			    var distance, coordinates;
			    d.kcoordinates = self.polarToCartesian(self.getAbsoluteCoordinates(d), canvasSize);
			    return "translate(" + d.kcoordinates.x + "," + d.kcoordinates.y + ")";
			})
            .attr("class", "node")
            .attr("id", function (d) {
                return "node_container" + d.id;
            });

        nodeEnter.append("path")
			.attr("d", d3.svg.symbol()
				.type(function (d) {
				    return d.new ? "triangle-up" : "circle";
				}))
			.attr("class", "c")
			//.attr("filter", "url(#dropShadow)")
			.on("mouseover", function (d) {

			    var desc;
			    desc = "";
			    if (d.description.length > 0) {
			        desc = "<br/>" + d.description;
			    }
			    tooltip.transition()
					.duration(200)
					.style("opacity", .8);
			    tooltip.html(d.name + desc)
					.style("left", (d3.event.pageX) + "px")
					.style("top", (d3.event.pageY - 28) + "px");
			})
			.on("mouseout", function (d) {
			    tooltip.transition()
					.duration(500)
					.style("opacity", 0);
			})
            .on("mouseup", function (d) {
                if (d.moved !== true) {

                    d3.event.preventDefault();
                    details.html('')
                        .style("left", (d3.event.pageX) + "px")
                        .style("top", (d3.event.pageY - 28) + "px");
                    self.focusNode(d);
                    details.transition()
                        .duration(200)
                        .style("opacity", .8)
                        .style("z-index", 101);
                }
            })
            .call(d3.behavior.drag()
                .on("drag", self.moveNode)
                .on("dragstart", self.pickupNode)
                .on("dragend", self.dropNode));

       nodeEnter.append("text")
            .attr("class", "nodetext")
            .attr("dx", 12)
            .attr("dy", ".35em")
            .text(function (d) {
                return d.shortcut;
            });


    };

    this.pickupNode = function (d) {
        var track;
        track = self.getTrack(d.track_id);
    };

    this.moveNode = function (d) {
        var dragTarget, x, y;
        d.moved = true;
        dragTarget = d3.select("#node_container" + d.id);

        d.kcoordinates.x += d3.mouse(dragTarget.node())[0];
        d.kcoordinates.y += d3.mouse(dragTarget.node())[1];

        dragTarget.attr("transform", "translate(" + d.kcoordinates.x + "," + d.kcoordinates.y + ")");
    };

    this.dropNode = function (d) {

        var result, polar, sector, track;

        if (d.moved === true) {
            result = new Object();
            result.id = d.id;

            polar = self.cartesianToPolar(d.kcoordinates, canvasSize);
            sector = self.getSectorByPolar(polar);
            track = self.getTrackByPolar(polar);


            if (sector != null && track != null) {
               
                d.sector_id = sector.id;
                d.track_id = track.zoomed ? d.track_id : (self.getTrackByPolar(polar)).id;
                d.absolutePolar = polar;
                d.coordinates = self.getRelativeCoordinates(d);

                result.sector_id = d.sector_id;
                result.track_id = d.track_id;
                result.coordinates = d.coordinates;
            } 
            else if(self.isNodeTrashedByCartesian(d.kcoordinates)) {
                result.state = NODESTATE.DELETED;
            }




            self.send(result, "items", "PUT", function (d) { self.showToast(d); });
            d.moved = false;
        }
    };

    this.focusNode = function (node) {
        var inputs, updatebtn, addKeyValueInput, detailsdiv;

        detailsdiv = $('#details');
        details.append("img")
            .attr("src", "img/close-icon.png")
            .attr("class", "close_icon")
            .on("click", function (d) {
                details.transition()
                    .duration(200)
                    .style("opacity", 0)
                    .style("z-index", -1)
            });
            
        inputs =  "<table>";
        $.each(node, function (k, v) {
            if ($.inArray(k, editableFields) !== -1) {
                inputs += ("<tr><td>" + k + "</td><td><div id=\"edit_" + k + "\" class=\"form_edit\" contenteditable=\"true\">" + (v !== null ? v : "") + "</div></td></tr>");
            }
        });
        inputs += "</table>";
        
        detailsdiv.append(inputs);

        d3.selectAll(".form_edit")
            .on("mouseover", function (d) {
                d3.select(d3.event.target)
                    .transition()
                    .duration(200)
                        .style("border-color", "red")
            })
            .on("mouseout", function (d) {
                d3.select(d3.event.target)
                    .transition()
                    .duration(200)
                        .style("border-color", "#323a6b")
            })

        updatebtn = document.createElement("input");
        updatebtn.type = "button";
        updatebtn.value = "Update";
        updatebtn.onclick = function () {
            var referenceNode;
            referenceNode = self.getNode(node.id);
            $.each(editableFields, function (i, field) {
                node[field] = $("#edit_" + field).text();
            });
            $.each(editableFields, function (i, field) {
                referenceNode[field] = node[field];
            });
            self.send(referenceNode, "items", "PUT", function (d) {
                self.showToast(d);

            });
        };
        detailsdiv.append(updatebtn);

    };

    this.showToast = function (message) {

        d3.select("#toast")
            .html(message)
            .transition()
            .duration(500)
        .style("opacity", .8)
        .each("end", function () {
            d3.select("#toast")
                .transition()
                .delay(3000)
                .duration(500)
            .style("opacity", 0);
        });
            
    };

    this.shadowDefs = function () {
        var dropShadowFilter;
        dropShadowFilter = container.append('svg:filter')
            .attr('id', 'dropShadow')
            .attr('filterUnits', "userSpaceOnUse")
            .attr('width', '100%')
            .attr('height', '100%');

        dropShadowFilter.append('svg:feGaussianBlur')
            .attr('in', 'SourceGraphic')
            .attr('stdDeviation', 2)
            .attr('result', 'blur-out');
        dropShadowFilter.append('svg:feColorMatrix')
            .attr('in', 'blur-out')
            .attr('type', 'hueRotate')
            .attr('values', 180)
            .attr('result', 'color-out');
        dropShadowFilter.append('svg:feOffset')
            .attr('in', 'color-out')
            .attr('dx', 3)
            .attr('dy', 3)
            .attr('result', 'the-shadow');
        dropShadowFilter.append('svg:feBlend')
            .attr('in', 'SourceGraphic')
            .attr('in2', 'the-shadow')
            .attr('mode', 'normal');
    };

    this.drawTracks = function () {

        var mode, count, fieldsize, innerR, outerR;

        mod = 1.681;
        count = tracks.length;
        fieldsize = (canvasSize / 2) - 5;
        innerR = 0;
        outerR = (fieldsize / count * mod);
        tracks.forEach(function (track) {

            track.innerRadius = innerR;
            track.outerRadius = outerR; 
            track.zoomed = false;

            container.append("path")
                .attr("id", "track" + track.id)
                .attr("d", self.drawDonut(innerR, outerR))
                .attr("class", "track")
                .attr("fill-opacity", 1 / count)
                .attr("transform", "translate(" + canvasSize / 2 + "," + canvasSize / 2 + ")")
                .data([track])
                .on("mouseover", self.fade(0.1))
                .on("mouseout", self.fade(1))
                .on("click", self.zoomTrack);

            container.append("text")
               .text(track.name)
               .attr("class", "trackTitle")
               .attr("text-anchor", "end")
               .attr("x", (canvasSize / 2) + outerR - 3)
               .attr("y", (canvasSize / 2) + 15)
               .data([track]);
        
            innerR = outerR;
            outerR += (fieldsize -outerR) /count  * mod;
            count--;
        });
    };

    this.zoomTrack = function (trackData) {
        var donut;

        if (trackData.zoomed == false) {
            donut = self.drawDonut(1, canvasSize / 2);
            trackData.zoomed = true;
        } else { 
            donut = self.drawDonut(trackData.innerRadius, trackData.outerRadius);
            trackData.zoomed = false;
        }

        d3.select("#track" + trackData.id)
            .data([trackData])
            .transition()
            .duration(500)
            .attr("d", donut);

        d3.selectAll(".track")
            .filter(function (d) {
                return !d.zoomed;
            })
            .style("visibility", trackData.zoomed ? "hidden" : null);

        d3.selectAll(".trackTitle")
            .transition()
            .duration(500)
               .attr("x", function (d) {
                   return trackData.zoomed ? canvasSize -3 : (canvasSize / 2) + d.outerRadius -3
               })

            .filter(function (d) {
                return trackData.id != d.id;
            })
            .style("visibility", trackData.zoomed ? "hidden" : null);
            
        d3.selectAll(".node")
            .filter( function(d) {
                return d.track_id != trackData.id
            })
            //.transition()
            //    .duration(500)
            //    .attr("opacity", trackData.zoomed ? "0" : "1");
            // this runs smoother, but is a little more clumsy
            .style("visibility", trackData.zoomed ? "hidden" : null);

        d3.selectAll(".node")
            .filter (function (d) {
                return d.track_id == trackData.id;
            })
            .transition()
            .duration(500)
            .attr("transform", function (d) {
                var distance, kCoordinates, aCoordinates;
                aCoordinates = self.getAbsoluteCoordinates(d);

                if (trackData.zoomed) {
                    distance = (canvasSize / 2) / 100 * d.coordinates.radial;
                }
                else {
                    //distance = trackData.innerRadius + self.relativeTrackDistance(trackData, d.coordinates.radial);
                    distance = aCoordinates.r;
                }
                kCoordinates = self.polarToCartesian({ "t": aCoordinates.t, "r": distance }, canvasSize);
                return "translate(" + kCoordinates.x + "," + kCoordinates.y + ")";
            })
    };

    this.drawSectors = function () {
        var origin, iterator;

        origin = canvasSize / 2;
        iterator = 0;

        sectors.forEach(function (sector) {

            var sectorBox, coordinates, heading;

            sector.startAngle = (360 / sectors.length) * iterator;
            sector.endAngle = sector.startAngle + (360 / sectors.length);
            sector.centerAngle = (sector.startAngle + sector.endAngle) / 2;
            sector.zoomed = false;
            //coordinates = self.polarToCartesian({ "r": origin, "t": sector.startAngle }, canvasSize);
            //heading = self.polarToCartesian({ "r": origin, "t": sector.centerAngle }, canvasSize);

            coordinates = self.polarToCartesian({ "r": origin, "t": sector.startAngle }, 0);
            heading = self.polarToCartesian({ "r": origin, "t": sector.centerAngle }, 0);
            
            sectorBox = container.append("g")
                .attr("transform", "translate(" + origin + "," + origin + ")");

            sectorBox.append("svg:line")
                 .attr("id", "sector"+sector.id)
                 .attr("x1", 0)//origin)
                 .attr("y1", 0)//origin)
                 .attr("x1", coordinates.x)
                 .attr("y1", coordinates.y)
                .data([sector])
                 .attr("class", "sector");


            sectorBox.append("text")
            	.text(sector.name)
            	.attr("class", "btitle")
            	.attr("transform", function (d) {
            	    return "translate(" + heading.x + "," + heading.y + ")rotate(" + (sector.centerAngle + 90) + ")"
            	});

            iterator++;
        });

    };

    this.drawTrash = function () {
        var trashG;
        trashG = container.append("g")
            .attr("id", "trash")
            .attr("transform", "translate(" + 50 + "," + canvasSize + ")");

        trashG.append("circle")
            .attr("id", "trashbin")
            .attr("r", 80)
            .attr("class", "control_field")
            .attr("cx", 40)
            .attr("cy", 40);

        trashG.append("text")
            .text("Trash Bin")
            .attr("x", 40)
            .attr("y", -45)
            .attr("class", "btitle");
    };

    this.drawNew = function () {

    };

    this.pickupNew = function (d) {
        var track;
        track = self.getTrack(d.track_id);
    };

    this.moveNew = function (d) {
        var dragTarget, x, y;
        d.moved = true;
        dragTarget = d3.select("#node_container" + d.id);

        d.kcoordinates.x += d3.mouse(dragTarget.node())[0];
        d.kcoordinates.y += d3.mouse(dragTarget.node())[1];

        dragTarget.attr("transform", "translate(" + d.kcoordinates.x + "," + d.kcoordinates.y + ")");
    };

    this.dropNew = function (d) {

        var result, polar, sectorId, trackId;

        if (d.moved === true) {
            polar = self.cartesianToPolar(d.kcoordinates, canvasSize);
            sectorId = (self.getSectorByPolar(polar)).id;
            trackId = (self.getTrack(d.track_id)).zoomed ? d.track_id : (self.getTrackByPolar(polar)).id;

            d.sector_id = sectorId;
            d.track_id = trackId;
            d.absolutePolar = polar;
            d.coordinates = self.getRelativeCoordinates(d);

            result = new Object();
            result.id = d.id;
            result.sector_id = d.sector_id;
            result.track_id = d.track_id;
            result.coordinates = d.coordinates;

            self.send(result, "items", "PUT", function (d) { self.showToast(d); });
            d.moved = false;
        }
    };
    this.zoomSector = function (sectorData) {
        var sector;

        if (sectorData.zoomed == false) {
            sectorData.zoomed = true;
        } else {
            sectorData.zoomed = false;
        }

        sector = d3.select("#sector" + sectorData.id)
            .data([sectorData])
            .transition()
                .duration(2000)
                .attrTween("transform", function (d, x, y) {
                    return d3.interpolateString("rotate(0)", "rotate(360)");
                });

        d3.selectAll(".sector")
            .filter(function (d) {
                return !d.zoomed;
            })
            .transition()
                .duration(500)
                .attrTween("transform", function (d, x, y) {
                    return d3.interpolateString("rotate(0)", "rotate(" + (d.startAngle + sectorData.startAngle)+ ")");
                });

        d3.selectAll(".node")
            .filter(function (d) {
                return d.sector_id != sectorData.id
            })
            .style("visibility", sectorData.zoomed ? "hidden" : null);

        d3.selectAll(".node")
            .filter(function (d) {
                return d.sector_id == sectorData.id;
            })
            .transition()
            .duration(500)
            .attr("transform", function (d) {
                var angle, kCoordinates, aCoordinates;
                aCoordinates = self.getAbsoluteCoordinates(d);

                if (sectorData.zoomed) {
                    angle = 360 / 100 * d.coordinates.angular;
                }
                else {
                    angle = aCoordinates.t;
                }
                kCoordinates = self.polarToCartesian({ "t": angle, "r": aCoordinates.r }, canvasSize);
                return "translate(" + kCoordinates.x + "," + kCoordinates.y + ")";
            })
    };

    this.polarToCartesian = function (polar, canvasSize) {
        var x, y;

        x = (polar.r * Math.cos(polar.t.toRad())) + canvasSize / 2;
        y = (polar.r * Math.sin(polar.t.toRad())) + canvasSize / 2;

        return { "x": x, "y": y }
    };

    this.cartesianToPolar = function (cartesian, canvasSize) {
        var r, t, x, y;
        
        x = cartesian.x - canvasSize / 2;
        y = cartesian.y - canvasSize / 2;
        r = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
        t = Math.atan2(y, x).toDeg();

        if (t < 0)
            t = 360 + t;

        return { "r": r, "t": t }
    }

    this.getAbsoluteCoordinates = function (node) {

        var track, sector, c;

        track = self.getTrack(node.track_id);
        sector = self.getSector(node.sector_id);
        
        c = new Object();
        c.r = track.innerRadius + (track.outerRadius - track.innerRadius) / 100 * node.coordinates.radial;
        c.t = sector.startAngle + (sector.endAngle - sector.startAngle) / 100 * node.coordinates.angular;

        return c;

    };

    this.getRelativeCoordinates = function (node) {
        var track, sector, c, polar;

        track = self.getTrack(node.track_id);
        sector = self.getSector(node.sector_id);

        c = new Object();

        if (!track.zoomed) {
            c.radial = (node.absolutePolar.r - track.innerRadius) / (track.outerRadius - track.innerRadius) * 100;
            c.angular = (node.absolutePolar.t - sector.startAngle) / (sector.endAngle - sector.startAngle) * 100;
        } else {
            c.radial = node.absolutePolar.r / (canvasSize /2) * 100;
            c.angular = (node.absolutePolar.t - sector.startAngle) / (sector.endAngle - sector.startAngle) * 100;
        }
            
        return c;
    };

    this.drawDonut = function (innerRadius, outerRadius) {
        return d3.svg.arc()
            .innerRadius(innerRadius)
            .outerRadius(outerRadius)
            .startAngle(0)
            .endAngle(10);
    };

    this.fade = function (opacity) {
        return function(g, i) {
            d3.selectAll(".track")
                .filter(function (d) {
                    return d.id != g.id;
                })
                .transition()
                .style("opacity", opacity);
        };

    };

    this.loadBootStrapData = function (callback) {

        if (nodes === undefined) {
            nodes = null;
            self.getNodes(self.loadBootStrapData, callback);
        }
        if (tracks === undefined) {
            tracks = null;
            self.getTracks(self.loadBootStrapData, callback);
        }
        if (sectors === undefined) {
            sectors = null;
            self.getSectors(self.loadBootStrapData, callback);
        }

        if ((nodes !== null && nodes !== undefined) &&
             (tracks !== null && tracks !== undefined) &&
            (sectors !== null && tracks !== undefined)) {
            callback();
        }
    };

    this.getNodes = function (callback, o){
        
        //d3.json("data/data.json?" + new Date().getTime(), function (error, root) {
        d3.json(rootUrl + "/rest/items?" + new Date().getTime(), function (error, root) {

            nodes = root;
            callback(o);
        });
    };

    this.getSectors = function (callback, o) {
        d3.json(rootUrl + "/rest/sectors?" + new Date().getTime(), function (error, root) {

            sectors = root;
            if (sectors != null) {
                sectors.sort(function (a, b) {
                    return a.weight - b.weight;
                });
            }
            callback(o);
        });
    };

    this.getTracks = function (callback, o) {
        d3.json(rootUrl + "/rest/tracks?" + new Date().getTime(), function (error, root) {

            tracks = root;
            
            if (tracks != null){
                tracks.sort(function (a, b) {
                    return a.weight - b.weight;
                });
            }
            callback(o);
        });
    }

    this.getNode = function (id) {
        var uinode, result;
        nodes.forEach(function (node) {
            if (node.id === id)
                uinode = node;
        });

        result = new Object();
        $.each(editableFields, function (i, field) {
            result[field] = uinode[field];
        });
        result.id = uinode.id;
        result.sector_id = uinode.sector_id;
        result.track_id = uinode.track_id;
        result.coordinates = uinode.coordinates;
        result.versions = uinode.versions;

        return result;
    };

    this.getSector = function (id) {
        var result;
        sectors.forEach(function (sector) {
            if (sector.id === id)
                result = sector;
        });
        return result;
    };

    this.getTrack = function (id) {
        var result;
        tracks.forEach(function (track) {
            if (track.id === id)
                result =  track;
        });
        return result;
    };

    this.getSectorByPolar = function (polar) {
        var result;

        sectors.forEach(function (sector) {
            //if (polar.t < 0) polar.t += 360;
            if (sector.startAngle <= polar.t &&
                sector.endAngle >= polar.t) {
                result = sector;
            }
        });

        return result;
    };

    this.getTrackByPolar = function (polar) {
        var result;

        tracks.forEach(function (track) {
            if (track.innerRadius <= polar.r &&
                track.outerRadius >= polar.r) {
                result = track;
            }
        });

        return result;
    };

    this.isNodeTrashedByCartesian = function (cartesian) {
        var result;

        result = (cartesian.x >= 50 && cartesian.x <= 130);
        result &= (cartesian.y >= canvasSize && cartesian.y <= canvasSize + 80);

        return result;
    };

    this.send = function (object, clazz, method, successCallback, errorCallback) {
        var data;
        data = JSON.stringify(object);
        $.ajax({
            url: rootUrl + '/rest/' + clazz,
            type: method,
            data: data,
            dataType: 'json',
            success: function (response) {
                if (response.message) {
                    successCallback(response.message);
                } else {
                    if (errorCallback !== undefined)
                        errorCallback(response.error);
                }
            }
        });
    };
}

$(function () {

    TR = new techRadar();
        TR.init();
});



