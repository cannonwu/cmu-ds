// Cannon Wu
// AndrewID: clwu
// ======================= FactSearch.java ==========================================
package org.myorg;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.util.*;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


/**
 * Code from: https://github.com/CMU-Heinz-95702/Project5
 * MapReduce application that will search for every occurrence of "fact" substring and output it
 * Search is case-insensitive
 */
public class FactSearch extends Configured implements Tool {

    /**
     * Subclass that extends Mapper class and objects with types: LongWritable, Text, IntWritable
     */
    public static class FactSearchMap extends Mapper<LongWritable, Text, Text, IntWritable>
    {
        // Setup binary variables; 1 = contains "fact"; 0 = does not contain "fact"
        private final static IntWritable one = new IntWritable(1);
        private final static IntWritable zero = new IntWritable(0); // initialize an IntWritable to hold numeric 0

        private Text word = new Text();

        /**
         * Searches each line for substring (ignore case) "fact" and writes the word
         * @param key = the line of the text file
         * @param value = the text value of the current line
         * @param context = used to write values to compute output
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
            String line = value.toString(); // get line text value
            StringTokenizer tokenizer = new StringTokenizer(line);  // tokenize the current line, each token is a word
            while(tokenizer.hasMoreTokens())
            {
                word.set(tokenizer.nextToken());    // iterate to next token (word)
                if (String.valueOf(word).toLowerCase().contains("fact")) {
                    context.write(word, one);   // if word contains fact, write one
                } else {
                    context.write(word, zero);  // if word does not contain fact, write zero
                }
            }
        }
    }

    /**
     * Subclass that extends Reducer class and objects with types: Text, IntWritable
     */
    public static class FactSearchReducer extends Reducer<Text, IntWritable, Text, IntWritable>
    {
        /**
         * Reducer job that will loop through each written value from the Mapper job.
         * Then, if sum = 1, write.
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
        {
            int sum = 0;       // init a value to hold the number of char occurrences
            for(IntWritable value: values)      // loop through each written value
            {
                sum += value.get();     // get value of word key, 1 for contains fact, 0 for does not contain fact
            }
            // if sum == 1, then key contains fact, write
            if (sum == 1) {
                context.write(key,null);
            }
        }

    }

    /**
     * Initiates MapReduce job and initializes each input, output params
     * @param args
     * @return report for successful run
     * @throws Exception
     */
    public int run(String[] args) throws Exception  {

        Job job = new Job(getConf());
        job.setJarByClass(FactSearch.class);
        job.setJobName("factsearch");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(FactSearchMap.class);
        job.setReducerClass(FactSearchReducer.class);


        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);


        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        boolean success = job.waitForCompletion(true);
        return success ? 0: 1;
    }


    /**
     * Starts runner that will Initiate MapReduce job
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        int result = ToolRunner.run(new FactSearch(), args);
        System.exit(result);
    }

}
