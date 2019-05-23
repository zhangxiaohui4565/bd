var appRouter = function (app) {
    app.get('/api/chart/pv', function(req, res) {
        var dbConn = req.con;
        dbConn.query('select * from pv_stat', function(err, rows) {
            if (err) {
                console.log(err);
                var data = ({ countResult: [] });
                res.status(400).json(data);
            } else {
                var data = rows.map(row => {
                    return ({name: row.name + "[" + row.product_id + "]", value: row.value})
                });
                res.status(200).json(data);
            }
        });
    });
    app.get('/api/chart/uv', function(req, res) {
        var dbConn = req.con;
        dbConn.query('select * from uv_stat', function(err, rows) {
            if (err) {
                console.log(err);
                var data = ({ countResult: [] });
                res.status(400).json(data);
            } else {
                var data = rows.map(row => {
                    return ({name: row.name + "[" + row.product_id + "]", value: row.value})
                });
                res.status(200).json(data);
            }
        });
    });
    app.get('/api/chart/visit_duration', function(req, res) {
        var dbConn = req.con;
        dbConn.query('select * from duration_stat', function(err, rows) {
            if (err) {
                console.log(err);
                var data = ({ countResult: [] });
                res.status(400).json(data);
            } else {
                var data = rows.map(row => {
                    return ({name: row.name + "[" + row.product_id + "]", value: row.value})
            });
            res.status(200).json(data);
            }
        });
    });
};

module.exports = appRouter;