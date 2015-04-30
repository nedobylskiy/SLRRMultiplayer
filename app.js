/*
 * Street Legal Racing: Redline Multiplayer
 * @url: https://github.com/jhonyxakep/SLRRMultiplayer
 * 
 * 
 */
 
'use strict'

var dnode = require('dnode');
var frida = require('frida');
var spawn = require('child_process').spawn;
var fs = require("fs");

var injectScript = fs.readFileSync('injectScript.js', "utf8");
var workingDir = 'E:/Street Legal Racing Experemental/';




var pos = { x: 0.0, y: 0.0, z: 0.0, sy: 0.0, sp: 0.0, sr: 0.0, angle: 0.0 };

process.on('uncaughtException', function (err) {
    console.log(err);
});




process.chdir(workingDir)
var gameProcess = spawn(
    workingDir+'StreetLegal_Redline.exe',
[], {
        stdio: 'inherit'
    });

AttachHook(gameProcess.pid)


function AttachHook(pid) {
    frida.attach(pid)
	 .then(function (session) {
        return session.createScript(injectScript);
    })
		.then(function (script) {
        script.events.listen('message', function (message, data) {
            console.log('HScript:', message, data);
        });
        script.load()
	    .then(function () {
            console.log('Hook script injected.');
        })
		.catch(function (error) {
            console.log('Hook Error:', error.message);
        });
    })
}



/*
var socket = dnode.connect(5004);
socket.on('remote', function (s) {
    s.getVersionInfo(function (ver) {
        console.log(ver);
    });

    s.setPos(pos,function (status) {
        console.log(status);
    });

});
 */

