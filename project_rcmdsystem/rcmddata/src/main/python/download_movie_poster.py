#!/usr/bin/env python
# -*- coding: utf-8 -*-
'''
@author: george
'''
# 读取 posters.csv 文件
# 针对每一行记录：
# 1.下载图片

import requests,os,sys

if(len(sys.argv) < 3):
    print('请输入正确的参数，调用示例：【download_movie_poster.py IMG_URL_FILE IMG_PATH】，其中IMG_URL_FILE为posters.csv文件路径，IMG_PATH为图片保存路径')
    os._exit(0)
    
imgURLFilePath = sys.argv[1];
imgDestDir = sys.argv[2];

imgUrlFile = open(imgURLFilePath)
for line in imgUrlFile:
    values = line.split(",")
    movieId = values[0]
    try:
        imgDistPath = imgDestDir+'/%s.jpg'%movieId
        if(os.path.exists(imgDistPath) == False):
            request = requests.get(values[1], timeout=20, stream=True)
            print "开始下载电影封面图片，电影id:" + movieId
            with open(imgDistPath, 'wb') as fh:
                for chunk in request.iter_content(1024 * 1024):
                    fh.write(chunk)
            print "结束下载电影封面图片，电影id:" + movieId        
    except BaseException, e:
        print '----下载电影封面失败，电影id:' + movieId, e
imgUrlFile.close()
