import requests

query = '''
{
    "from": 0,
    "size": 100,
    "query": {
      "bool": {
        "filter": [
          {
            "term": {
              "card_bank_code.keyword": "BPI"
            }
          },
          {
            "term": {
              "gender.keyword": "Male"
            }
          },
          {
            "range": {
              "family_income": {
                "from": 50000,
                "to": null,
                "include_lower": true,
                "include_upper": true
              }
            }
          }
        ]
      }
    },
    "_source": {
      "includes": [
        "family_income",
        "user_id",
        "card_bank_code",
        "gender"
      ],
      "excludes": []
    }
  }
'''
query = query.replace('\n', '').replace(' ', '')

body = {
    'name': 'crm select people job',
    'schedule': 'once',
    'query': query,
    'status': 'active'
}
print(requests.post('http://localhost:5000/jobs', body))

