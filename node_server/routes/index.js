var express = require('express');
var router = express.Router();
var path = require("path")
var app = require("../app")

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

/* GET anything else. */
router.get('/:url', function(req, res, next) {
  res.status(200).sendFile((app.abs_dir+ "/data/" + req.params.url));
});



module.exports = router;
