package com.gupao.bd.sample.hdfs;

import java.util.LinkedList;

/**
 * 管理元数据
 * 磁盘
 * @author Administrator
 * 
 * 这段代码我是模仿hadoop的源码的写的，大家一定要掌握
 * 后面我们要修改这段代码
 * 其实我现在写的这段代码跟hadoop的源码的相似有90%的相似
 * 5%
 * 5%
 *
 */
public class FSEdit {
	public static void main(String[] args) {
		FSEdit fs=new FSEdit();
		for (int i = 0; i < 1000; i++) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 100; j++) {
						fs.logEdit("日志");
					}
					
				}
			}).start();
		}
	}
	
	
	
	long taxid=0L;
	//缓存写入实体类进行内存交换和磁盘写出等操作
	DoubleBuffer doubleBuffer=new DoubleBuffer();
	//每个线程自己拥有的副本
	ThreadLocal<Long> threadLocal=new ThreadLocal<Long>();
	//是否后台正在把数据同步到磁盘上
	public boolean isSyncRunning =false;
	//正在同步磁盘 的内存块里面最大的一个ID号。
	long maxtaxid=0L;
	boolean isWait=false;
	/**
	 * 写元数据日志的核心方法
	 * @param log
	 */
	private void logEdit(String log) {
//这把锁里面是往内存里面写数据
		//能支持很高的并发。
		//超高并发
		//线程1 1 线程2 2  线程3 3
		//线程4 5
		synchronized (this) {
			taxid++;
			threadLocal.set(taxid);
			EditLog editLog=new EditLog(taxid,log);
			//往内存里面写东西
			doubleBuffer.write(editLog);
			
		} //释放锁
		//把数据持久化到磁盘
		//代码运行到这儿的时候 currentBuffer 内存里面已经有3条数据了。
		
		//没有加锁，这叫分段加锁
		
		//重新加锁
		logFlush();
		
	}
	
	
	
	private void logFlush() {
		//重新加锁 线程2
		synchronized(this) {
			if(isSyncRunning) {//false true
				//获取当前线程的是事务ID
				//2
				long localTaxid=threadLocal.get();
				//2 < 3
				//5 < 3
				if(localTaxid <= maxtaxid) {
					return ;
					
				}
				if(isWait) {
					return;
				}
				isWait=true;
				while(isSyncRunning) {
					try {
						//一直等待
						//wait这个操作是释放锁
						this.wait(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				isWait=false;
						
				//
			}
			//代码首次执行
			//syncBuffer(1,2,3)  
			doubleBuffer.exchange();
			//maxtaxid = 3// 更新事务id
			if(doubleBuffer.syncBuffer.size() > 0) {
				maxtaxid=doubleBuffer.getMaxTaxid();
			}
			isSyncRunning=true;
			
		}//释放锁
		//把数据持久化到磁盘，比较耗费性能的。
		// 1 2 3写到磁盘
		doubleBuffer.flush();
		//分段加锁
		synchronized (this) {
			//修改标志位
			isSyncRunning=false;
			//唤醒wait；
			this.notifyAll();
			
		}
		
		
	}



	/**
	 * 我用面向对象的思想，设计一个对象
	 * 代表着一条元数据信息
	 * @author Administrator
	 *
	 */
	public class EditLog{
		//事务的ID
		public long taxid;
		public String log;
		
		public EditLog(long taxid, String log) {
			this.taxid = taxid;
			this.log = log;
		}

		@Override
		public String toString() {
			return "EditLog [taxid=" + taxid + ", log=" + log + "]";
		}
		
	}
	
	public class DoubleBuffer{
		//写数据,有序队列
		LinkedList<EditLog> currentBuffer=new LinkedList<EditLog>();
		//用来把数据持久化到磁盘上面的内存
		LinkedList<EditLog> syncBuffer=new LinkedList<EditLog>();
		/**
		 * 写元数据信息
		 * @param editLog
		 */
		public void write(EditLog editLog){
			currentBuffer.add(editLog);
			
		}
		/**
		 * 把数据写到磁盘
		 */
		public void flush() {
			for(EditLog editLog:syncBuffer) {
				//把打印出来，我们就认为这就是写到磁盘了
				System.out.println(editLog);	
			}
			syncBuffer.clear();
			
		}
		/**
		 * 交换一下内存
		 */
		public void exchange() {
			LinkedList<EditLog> tmp=currentBuffer;
			currentBuffer=syncBuffer;
			syncBuffer=tmp;
		}
		/**
		 * 获取到正在同步数据的内存里面事务ID最大的ID
		 * @return
		 */
		public long getMaxTaxid() {
			return syncBuffer.getLast().taxid;
		}
		
	}

}
