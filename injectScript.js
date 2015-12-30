var scriptVerion = '0.3';
console.log('>>> Multiplayer hook script <<<');
console.log('>>> Script version: ' + scriptVerion + ' <<<');

var savedPointer = "";

var p = Memory.allocAnsiString("DEMO");
var pattern = p.toMatchPattern();

var dummy = Memory.allocAnsiString("\\\\nothing\\dev\\null");

/*var timer = setInterval(function () {

	console.log(savedPointer);
	console.log(p.toString());
	console.log(pattern);
	console.log("Start memory scan");
	Memory.scan(ptr(savedPointer), 4294967296, pattern, {
		onMatch: function (address, size) {
			console.log("something found");
			console.log(address);
			clearInterval(timer);
		},
		onComplete: function () {
			console.log("Memory scan complete");
		},
		onError: function (reason) {
			console.log(reason);
		}
	});

}, 30000);*/

//var p = ptr(Memory.allocAnsiString("aabbccddeeffggg"));
/*var p = Memory.allocAnsiString("DEMO");//ptr();
var pattern = p.toMatchPattern();

console.log(p);
console.log(pattern);

var timer = setInterval(function () {

	var modules = Process.enumerateModulesSync();

	for(var a in modules){

		//if(modules[a].name=="StreetLegal_Redline.exe") {
		//	console.log(modules[a].name);
			Memory.scan(modules[a].base, modules[a].size, pattern, {
				onMatch: function (address, size) {
					console.log("something found");
					console.log(address);
					clearInterval(timer);
				},
				onComplete: function () {
					//console.log("Memory scan complete");
				},
				onError: function (reason) {
					console.log(reason);
				}
			});
		//}
	}

}, 3000);*/

var message="";

Interceptor.attach(Module.findExportByName('kernel32.dll', 'CreateFileA'), {
	onEnter: function onEnter(args) {
		//this.fileDescriptor = args[0].toInt32();
		message = Memory.readUtf8String(args[0]);
		//console.log(Memory.readUtf8String(args[0]));

		//savedPointer = args[0].toString();

		//Block error logging :(
		/*if (message.indexOf('error.log') != -1) {
		 args[0] = Memory.allocAnsiString("\\\\nothing\\dev\\null");
		 }*/

		/*if (message.indexOf('trololo') != -1) {
		 this.savePointer = true;
		 console.log('trololo pointer saved');
		 //args[0] = Memory.allocAnsiString("\\\\nothing\\dev\\null");
		 }*/
		if (message.indexOf('DTM^') != -1) {
			message = message.split('^');
			send({name: message[1], data: message[2]});
			//args[0] = dummy;
		}

	},
	onLeave: function onLeave(retval) {
		/*if (this.savePointer) {
		 savedPointer = retval.toInt32();
		 console.log('trololo pointer saved2');
		 }
		 if (retval.toInt32() > 0) {
		 //console.log(retval.toInt32());
		 }*/
	}
});

Interceptor.attach(Module.findExportByName('kernel32.dll', 'ReadFile'), {
	onEnter: function onEnter(args) {
		//this.fileDescriptor = args[0].toInt32();
		//var message = Memory.readUtf8String(args[1]);
		//if (args[0].toInt32() == savedPointer) {
		//console.log('TROLOLO');
		//console.log("'"+Memory.readAnsiString(args[1])+"'");
		//args[0] = Memory.allocAnsiString("SDAT     -278.453;9.800;1033.002            ");
		//args[3] = ptr("1");
		//}

		/*if(Memory.readAnsiString(args[1]).indexOf('DTM')!=-1){


		 this.args=args;
		 //console.log(Memory.readAnsiString(args[1]));
		 }*/

//qwerty123321qwerty
	},
	onLeave: function onLeave(retval) {
		//if(this.args)
		//	Memory.writeAnsiString(args[1], "DTM:sosii");
		//this.args[1] = Memory.allocAnsiString("DTM:sosii");
	}
});


//var f = new NativeFunction(ptr("%s"), 'void', ['int']);
//f(1911);
