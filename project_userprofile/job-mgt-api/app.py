from flask import Flask
from flask_restful import reqparse, abort, Api, Resource
from elasticsearch import Elasticsearch
from elasticsearch.exceptions import NotFoundError
from datetime import datetime

app = Flask(__name__)
api = Api(app)

es = Elasticsearch('http://192.168.64.2:9200')


def abort_if_job_doesnt_exist(job_id):
    abort(404, message="Job {} doesn't exist".format(job_id))

parser = reqparse.RequestParser()
parser.add_argument('name')
parser.add_argument('schedule')
parser.add_argument('query')
parser.add_argument('status')


# Job
# shows a single job item and lets you delete a job item
class Job(Resource):
    def get(self, job_id):
        try:
            ret_val = es.get(index='job-def', doc_type='doc', id=job_id)['_source']
            ret_val['id'] = job_id
            return ret_val
        except NotFoundError as exc:
            print(exc)
            abort_if_job_doesnt_exist(job_id)

    def delete(self, job_id):
        try:
            ret_val = es.delete(index='job-def', doc_type='doc', id=job_id)

            print(ret_val)
            if ret_val['result'] == 'deleted':
                print('deleted job definition: {}'.format(job_id))
                return '', 204
            else:
                return '', 500
        except NotFoundError as err:
            print(err)
            abort_if_job_doesnt_exist(job_id)


# JobList
# shows a list of all jobs, and lets you POST to add new jobs
class JobList(Resource):
    def get(self):
        ret_val = es.search(index="job-def", body={"query": {"match_all": {}}})

        job_list = []
        for hit in ret_val['hits']['hits']:
            job_def = hit["_source"]
            job_def['id'] = hit['_id']
            job_list.append(job_def)

        print(job_list)

        return job_list

    def post(self):
        args = parser.parse_args()
        print(args)
        ret_val = es.index(index='job-def', doc_type='doc', body=args)
        print(ret_val)
        job_id = ret_val['_id']

        if job_id:
            schedule = args['schedule']
            # 判断Job实例开始时间
            if schedule == 'once':
                start_time = datetime.now()
            else:
                hour = int(schedule)
                start_time = datetime.now().replace(hour=hour, minute=0)

            job_instance = {
                'job_id': job_id,
                'start_time': start_time,
                'schedule': schedule,
                'status': 'active',
                'query': args['query']
            }
            ret_val = es.index(index='job-instance', doc_type='doc', body=job_instance)
            job_instance_id = ret_val['_id']

            return (job_id, job_instance_id), 201
        else:
            return 'job未创建', 500

##
## Actually setup the Api resource routing here
##
api.add_resource(JobList, '/jobs')
api.add_resource(Job, '/jobs/<job_id>')


if __name__ == '__main__':
    app.run(debug=True)
