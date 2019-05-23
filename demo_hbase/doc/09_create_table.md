create 'gupao:students',{NAME=>'info',VERSIONS => 3},{NAME=>'info2',VERSIONS => 1},SPLITS => ['3', '5', '7']

put 'gupao:students','1','info:name','Tom'

put 'gupao:students','2','info:name','Jack'

put 'gupao:students','9','info:name','Lily'

deleteall 'gupao:students','1'

flush 'regionname'

hbase hfile -f /hbase/data/gupao/students/dd8bac7871fdcf68f148448f5870e3fa/info/661658501f6a4495b409461e02faab4f --printkv