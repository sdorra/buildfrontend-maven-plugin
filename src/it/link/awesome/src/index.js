var fs = require("fs");
var path = require("path");

module.exports = function() {
    if (!fs.existsSync("target")) {
        fs.mkdirSync("target");
    }
    var file = path.join("target", "awesome.txt");
    fs.writeFileSync(file, "awesome");
};
