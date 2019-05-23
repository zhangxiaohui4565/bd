from pyhive import hive

host_name = '127.0.0.1'
port = 10000
user = 'hive'
password = 'hive'
database = 'default'

def hiveconnection(host_name, port, user,password, database):
    conn = hive.Connection(host=host_name, port=port, username=user, password=password,
                           database=database, auth='CUSTOM')
    cur = conn.cursor()
    cur.execute('show tables')
    result = cur.fetchall()
    for row in result:
        print(row[0])

    print('*********')

    cur.execute('select * from works limit 10')
    result2 = cur.fetchall()
    for row in result2:
        print(str(row[0]), str(row[1]), row[2])

# Call above function
hiveconnection(host_name, port, user,password, database)
