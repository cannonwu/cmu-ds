// Cannon Wu
// AndrewID: clwu
// ======================= LetterCounter.java ==========================================
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
 * MapReduce application that will compute the total occurrences for each letter in a given file.
 */
public class LetterCounter extends Configured implements Tool {

    /**
     * Subclass that extends Mapper class and objects with types: LongWritable, Text, IntWritable
     */
    public static class LetterCounterMap extends Mapper<LongWritable, Text, Text, IntWritable>
    {
        private final static IntWritable one = new IntWritable(1);
        private Text letter = new Text();

        /**
         * Splits each line of the document into a charArray for context writing.
         * @param key = the line of the text file
         * @param value = the text value of the current line
         * @param context = used to write values to compute output
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
        {
            String line = value.toString();     // get line text value
            char[] letterArray = line.toCharArray();    // split text into charArray
            for (char c : letterArray) {                // for each letter in the charArray
                letter.set(String.valueOf(c));          // set letter (Text obj) to String val of char
                context.write(letter, one);             // context.write the current letter
            }
        }
    }

    /**
     * Subclass that extends Reducer class and objects with types: Text, IntWritable
     */
    public static class LetterCounterReducer extends Reducer<Text, IntWritable, Text, IntWritable>
    {
        /**
         * Reducer job that will loop through each written value from the Mapper job and sum the occurrences
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
        {
            int sum = 0;       // init a value to hold the number of char occurrences
            for(IntWritable value: values)      // loop through each written value (one,one,...)
            {
                sum++;  // increment 1 for each element
            }
            context.write(key, new IntWritable(sum));   // write out the sum for that letter
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
        job.setJarByClass(LetterCounter.class);
        job.setJobName("lettercounter");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(LetterCounterMap.class);
        job.setReducerClass(LetterCounterReducer.class);


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
        int result = ToolRunner.run(new LetterCounter(), args);
        System.exit(result);
    }

}
