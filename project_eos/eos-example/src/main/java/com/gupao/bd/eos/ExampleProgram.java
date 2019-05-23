package com.gupao.bd.eos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * 日志输出演示工程
 */
public class ExampleProgram {
    private final static Logger logger = LoggerFactory.getLogger(ExampleProgram.class);
    private final static Random random = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {
        logger.info("eos-example program started");
        logger.info("This program demonstrate how to use eos-client to output log");
        logger.info("Caution: this program never stop");
        logger.info("Stop it by ctrl + c or kill");

        // this program never stop
        while (true) {
            logger.info("Start to log in loop");
            logger.warn("This loop never stop");
            logRandom(nextRandom());
            sleepRandom(nextRandom());
            logRandomCh(nextRandom());
            sleepRandom(nextRandom());
            logException(nextRandom());
            sleepRandom(nextRandom());
            multiThreadLog();
            sleepRandom(nextRandom());
        }
    }

    private static void multiThreadLog() {
        Thread thread = new Thread(() -> {
            try {
                logger.warn("This code fragment will throw an IllegalArgumentException");
                throw new IllegalAccessException("illegal access");
            } catch (IllegalAccessException e) {
                logger.error("cal error: ", e);
            }

            logger.info("log message in thread: " + Thread.currentThread());
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static int nextRandom() {
        int ran = random.nextInt();
        return (0 < ran) ? ran : -ran;
    }

    private static void sleepRandom(int ran) throws InterruptedException {
        Thread.sleep(ran % 3000 );
    }

    private static void logException(int ran) throws InterruptedException {
        try {
            logger.warn("This code fragment will throw an IllegalArgumentException");
            int a = 12, b = 0;
            int c = a / b;
            logger.info("c:" + c);
        } catch (RuntimeException e) {
            logger.error("cal error: ", e);
        }
        sleepRandom(ran);
        try {
            logger.warn("This code fragment will throw an IllegalArgumentException");
            throw new IllegalArgumentException("arg is invalid");
        } catch (RuntimeException e) {
            logger.error("cal error: ", e);
        }
    }

    private static void logRandomCh(int ran) {
        String[] sentences = {
                "term 查询在倒排索引中查找 XHDK-A-1293-#fJ3 然后获取包含该 term 的所有文档。本例中，只有文档 1 满足我们要求。",
                "一旦为每个查询生成了 bitsets ，Elasticsearch 就会循环迭代 bitsets 从而找到满足所有过滤条件的匹配文档的集合。" +
                        "执行顺序是启发式的，但一般来说先迭代稀疏的 bitset （因为它可以排除掉大量的文档）",
                "最终组合的结果是一个 constant_score 查询",
                "标题 title 字段是一个 string 类型（ analyzed ）已分析的全文字段，这意味着查询字符串本身也应该被分析。",
                "将查询的字符串 QUICK! 传入标准分析器中，输出的结果是单个项 quick 。因为只有一个单词项，所以 match 查询执行的是单个底层 term 查询。",
                "即词 quick 在相关文档的 title 字段中出现的频率）和反向文档频率（inverse document frequency",
                "不去匹配 brown OR dog ，而通过匹配 brown AND dog 找到所有文档"
        };

        int idx = ran % sentences.length;
        logger.info(sentences[idx]);
    }

    private static void logRandom(int ran) {
        String[] sentences = {
                "In the beginning God created the heaven and the earth.",
                "And the earth was without form, and void; " +
                        "and darkness was upon the face of the deep. " +
                        "And the Spirit of God moved upon the face of the waters.",
                "And God said, Let there be light: and there was light.",
                "And God saw the light, that it was good: and God divided the light from the darkness.",
                "And God called the light Day, and the darkness he called Night. " +
                        "And the evening and the morning were the first day.",
                "And God said, Let there be a firmament in the midst of the waters, and let it divide the waters from the waters.",
                "And God made the firmament, and divided the waters which were under the firmament from the waters which were above the firmament: and it was so.",
                "And God called the firmament Heaven. And the evening and the morning were the second day.",
                "And God said, Let the waters under the heaven be gathered together unto one place, and let the dry land appear: and it was so.",
                "And God called the dry land Earth; and the gathering together of the waters called he Seas: and God saw that it was good.",
                "And God said, Let the earth bring forth grass, the herb yielding seed, and the fruit tree yielding fruit after his kind, whose seed is in itself, upon the earth: and it was so.",
        };
        int idx = (int) (ran % sentences.length);
        logger.info(sentences[idx]);
    }
}
