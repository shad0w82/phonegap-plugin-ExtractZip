cordova.define("cordova.plugin.ExtractZip.ExtractZip", function(require, exports, module) { 
/*
 * @author Evgeniy Lukovsky
 * @contribute Gianfranco Caputo
 * */

	var exec = require('cordova/exec');

	var ExtractZip=function(){
	};

	ExtractZip.prototype.extract = function(file, dest, successCallback, errorCallback) 
	{
		return cordova.exec(successCallback, errorCallback, "ExtractZip", "extract", [file,dest]);
	};
        
        ExtractZip.prototype.extractFromArray = function(byteArray, dest, successCallback, errorCallback) 
	{
		return cordova.exec(successCallback, errorCallback, "ExtractZip", "extractArray", [byteArray,dest]);
	};

	ExtractZip.prototype.getTempDir = function(dirName, successCallback, errorCallback) 
	{
		return cordova.exec(successCallback, errorCallback, "ExtractZip", "getTempDir", [dirName]);
	};

	module.exports= new ExtractZip();


});
