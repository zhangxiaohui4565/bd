# -*- coding: utf-8 -*-
from flask import Flask
from flask import request
from flask import Response
from flask import render_template
import json
import mysql.connector                 

config = {
    'host': '127.0.0.1',
    'user': 'root',
    'password': '',
    'port': 3306,
    'database': 'web_data',
    'charset': 'utf8'
}

cnxpool = mysql.connector.pooling.MySQLConnectionPool(pool_name = "mypool", pool_size = 3, **config)


app = Flask(__name__)


@app.route('/echarts_data/hello')
def hello_word():
    return 'Hello World!'


@app.route('/echarts_data/index')
def index():
    return render_template('index.html')


@app.route('/echarts_data/pv')
def get_pv():
    seriesData = []

    cnx = cnxpool.get_connection() 
    cursor = cnx.cursor()
    try:
        sql_query = 'select url, count from web_pv'
        cursor.execute(sql_query)
        for url, count in cursor:
            seriesData.append({"name": url, "value": count})
    except mysql.connector.Error as e:
        print('query error! e = [%s]' % e)
        return Response(status=500)
    finally:
        cursor.close()
        cnx.close()

    datas = {
        "seriesData": seriesData
    }
    content = json.dumps(datas)
    return add_headers(content)


@app.route('/echarts_data/uv')
def get_uv():
    seriesData = []

    cnx = cnxpool.get_connection()
    cursor = cnx.cursor()
    try:
        sql_query = 'select url, count from web_uv'
        cursor.execute(sql_query)
        for url, count in cursor:
            seriesData.append({"name": url, "value": count})
    except mysql.connector.Error as e:
        print('query error! e = [%s]' % e)
        return Response(status=500)
    finally:
        cursor.close()
        cnx.close()

    datas = {
        "seriesData": seriesData
    }
    content = json.dumps(datas)
    return add_headers(content)



def add_headers(content):  
    resp = Response(response=content, mimetype="application/json")
    resp.headers['Access-Control-Allow-Origin'] = '*'
    return resp


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=9090)
