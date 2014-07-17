phonegap-plugin-ExtractZip
==========================
Modified version of https://github.com/fiscal-cliff/phonegap-plugin-ExtractZip

Now it supports Android and iOS within Phonegap 3.

there are two methods:
* extract(fileName, destination, successCB, failCB)
* getTempDir(name, successCB, failCB)

Added third method [ANDROID ONLY!]:
* extractFromArray(byteArray, destination, successCB, failCB)

Installing
======
You may use phonegap CLI as follows:

<pre>
➜ phonegap local plugin add https://github.com/shad0w82/phonegap-plugin-ExtractZip.git
[phonegap] adding the plugin: https://github.com/shad0w82/phonegap-plugin-ExtractZip.git
[phonegap] successfully added the plugin
</pre>

Using
====
```javascript
		document.addEventListener('deviceready', onDeviceReady);
		function onDeviceReady() {
			document.body.style.background = 'red'
			var xhr = new XMLHttpRequest();
			xhr.open('GET', 'raw/'+path, true);
			xhr.responseType = 'arraybuffer';
			xhr.overrideMimeType('text/plain; charset=x-user-defined'); //fetch db file as binary
			xhr.onload = function(e) {
				window.dbFileuInt8Array = new Uint8Array(this.response);
				ExtractZip.extractFromArray(
		                    _arrayBufferToBase64(dbFileuInt8Array),
		                    "/sdcard/DbDieta/raw/Jigsaw/", 
		                    function(s) { //Success 
		                    },
		                    function(s) { //Fail
		                    }
		                );
		        };
		}
		
		function _arrayBufferToBase64( bytes ) {
	            var binary = '';
	            var len = bytes.byteLength;
	            for (var i = 0; i < len; i++) {
	                binary += String.fromCharCode( bytes[ i ] )
	            }
	            return window.btoa( binary );
	        }
		
```
