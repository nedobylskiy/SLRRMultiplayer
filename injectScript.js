var scriptVerion = '0.3';
console.log('>>> Multiplayer hook script <<<')
console.log('>>> Script version: '+ scriptVerion +' <<<')

/*
Interceptor.attach(Module.findExportByName('kernel32.dll', 'CreateFileA'), {
    onEnter: function onEnter(args) {
        //this.fileDescriptor = args[0].toInt32();
	console.log(Memory.readUtf8String(args[0]))
    },
    onLeave: function onLeave(retval) {
        if (retval.toInt32() > 0) {
		//console.log(retval.toInt32());
        }
    }
});*/


var f = new NativeFunction(ptr("%s"), 'void', ['int']);
f(1911);
