import logging

import requests
from airflow import AirflowException
from airflow.hooks.http_hook import HttpHook
import json

import datetime as dt
from dateutil.tz import gettz, tzoffset


def json_to_dt(obj):
    if obj.pop('__type__', None) != "datetime":
        return obj
    zone, offset = obj.pop("tz")
    obj["tzinfo"] = tzoffset(zone, offset)
    return dt.datetime(**obj)


def dt_to_json(obj):
    if isinstance(obj, dt.datetime):
        return int(obj.timestamp() * 1000)
    else:
        raise TypeError("Cant serialize {}".format(obj))


class ESHook(HttpHook):
    def search(self, index_and_type, args):
        session = self.get_conn({})

        url = self.base_url + '/' + index_and_type + '/_search'

        req = requests.Request('GET', url, json=args)
        prep_req = session.prepare_request(req)

        resp = session.send(prep_req)
        try:
            resp.raise_for_status()
        except requests.exceptions.HTTPError:
            logging.error("HTTP error: " + resp.reason)
            raise AirflowException(str(resp.status_code) + ":" + resp.reason)

        return json.loads(resp.content)

    def update(self, index_and_type, id, body):
        session = self.get_conn({})

        url = self.base_url + '/' + index_and_type + '/' + id

        req = requests.Request('POST', url, json=body)
        prep_req = session.prepare_request(req)

        resp = session.send(prep_req)
        try:
            resp.raise_for_status()
        except requests.exceptions.HTTPError:
            logging.error("HTTP error: " + resp.reason)
            raise AirflowException(str(resp.status_code) + ":" + resp.reason)

        return json.loads(resp.content)

    def index(self, index, doc_type, body={}):
        """Create new document."""
        session = self.get_conn({})

        query = json.dumps(body, default=dt_to_json)
        url = '{}/{}/{}'.format(self.base_url, index, doc_type)

        print('query: ' + query)
        # 由于requests里使用的json.dumps方法没法设置default参数, 序列化datetime会有问题
        req = requests.Request('POST', url, data=query, headers={'Content-Type': 'application/json'})
        prep_req = session.prepare_request(req)

        resp = session.send(prep_req)

        try:
            resp.raise_for_status()
        except requests.exceptions.HTTPError:
            logging.error("HTTP error: " + resp.reason)
            raise AirflowException(str(resp.status_code) + ":" + resp.reason)

        print('resp[{}]: {}'.format(resp.status_code, resp.text))

    def bulk_index(self, index, doc_type, rows_list=[]):
        """Bulk create new document."""
        session = self.get_conn({})

        body_text = ''
        meta_line = '''{{ "index" : {{ "_index" : "{}", "_type" : "{}" }} }}\n'''.format(index, doc_type)
        for row_dict in rows_list:
            row_json = json.dumps(row_dict, default=dt_to_json)
            body_text = body_text + meta_line + row_json + '\n'

        # 由于requests里使用的json.dumps方法没法设置default参数, 序列化datetime会有问题
        url = '{}/_bulk'.format(self.base_url)

        req = requests.Request('POST', url, data=body_text, headers={'Content-Type': 'application/x-ndjson'})
        prep_req = session.prepare_request(req)

        resp = session.send(prep_req)

        try:
            resp.raise_for_status()
        except requests.exceptions.HTTPError:
            logging.error("HTTP error: " + resp.reason)
            raise AirflowException(str(resp.status_code) + ":" + resp.reason)

        print('resp[{}]'.format(resp.status_code))
