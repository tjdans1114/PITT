module.exports = function(app)
{
     app.get('/',function(req,res){
        res.render('index.html')
     });
     app.get('/:url',function(req,res){
        res.render(req.params.url);
    });
}
