#!/usr/bin/env python
# -*- coding: utf-8 -*-
'''
@author: george
'''
# 读取 links.csv 文件
# 针对每一行记录：
# 1.爬取imdb网页
# 2.分析，提取poster图片地址
# 3.存储图片地址

import urllib2,sys,os
from bs4 import BeautifulSoup

def fetchMoviePoster(imdbId):
    movieUrl = imdbMovieBaseUrl + str(imdbId)
    print "IMDB电影地址: " + movieUrl
    html = urllib2.urlopen(movieUrl, timeout=timeoutInSeconds).read() 
    soup = BeautifulSoup(html, "html")
    posterTag = soup.find("div", class_="poster")
    return posterTag.a.img['src']

if(len(sys.argv) < 3):
    print('请输入正确的参数，调用示例：【get_movie_poster.py MOVIE_DATA_PATH RESULT_PATH】，其中MOVIE_DATA_PATH为电影文件路径，RESULT_PATH为结果保存路径')
    os._exit(0)
    
srcDataDir = sys.argv[1];
resultDir = sys.argv[2];

imdbMovieBaseUrl = "https://www.imdb.com/title/tt"
timeoutInSeconds = 30

inputFile = open(srcDataDir + "links.csv")
outputFile = open(resultDir + "posters.csv", 'w')
failedRecordFile = open(resultDir + "failed_records.csv", 'w')
i = 0
for line in inputFile:
    if(i == 0):
        #去掉标题行   
        i = i+1
        continue
    #每行数据格式为：movieId,imdbId,tmdbId
    values = line.split(",")
    movieId = values[0]
    imdbId = values[1]
    print "开始获取电影的封面地址，电影id:" + movieId
    try:
        imgUrl = fetchMoviePoster(imdbId)
        outputFile.write(movieId + "," + imgUrl + "\n")
    except BaseException, e:
        print '----获取电影封面地址失败:' + movieId, e
        failedRecordFile.write(line)
    print "结束获取电影的封面地址，电影id::" + movieId
inputFile.close()
outputFile.close()
failedRecordFile.close()  

    
    