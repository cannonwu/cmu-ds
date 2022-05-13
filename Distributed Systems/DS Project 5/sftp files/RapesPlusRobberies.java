// Cannon Wu
// AndrewID: clwu

package edu.cmu.andrew.clwu;

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
 * MapReduce application that will compute the total occurrences for crimes involving rape or robbery.
 */
public class RapesPlusRobberies extends Configured implements Tool {

    /**
     * Subclass that extends Mapper class and objects with types: LongWritable, Text, IntWritable
     */
    public static class RapesPlusRobberiesMap extends Mapper<LongWritable, Text, Text, IntWritable>
    {
        private final static IntWritable one = new IntWritable(1);
        private Text cell = new Text(); // a variable to hold the cell term for writing

        /**
         * Splits each line of the document into an Array with tab delimiter
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
            String[] lineArray = line.split("\t");  // convert line into an array, split on /t
            // if the 5th column (index 4) equals rape or robbery
            if (lineArray[4].trim().equals("RAPE") || lineArray[4].trim().equals("ROBBERY")) {
                cell.set("RAPES+ROBBERIES");    // set key Text
                context.write(cell,one);    // write key and value of one
            }
        }
    }

    /**
     * Subclass that extends Reducer class and objects with types: Text, IntWritable
     */
    public static class RapesPlusRobberiesReducer extends Reducer<Text, IntWritable, Text, IntWritable>
    {
        /**
         * Reducer job that will loop through each written value from the Mapper job and sum the occurrences
         * It will read something like: [RAPES+ROBBERIES,[one,one,one,...]]
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
        {
            int sum = 0;    // initialize numeric value to hold computed sum
            for(IntWritable value: values)  // for each mapped value
            {
                sum += value.get(); // sum the mapped values
            }
            context.write(key, new IntWritable(sum));   // key = RAPES+ROBBERIES and sum has been computed
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
        job.setJarByClass(RapesPlusRobberies.class);
        job.setJobName("rapesplusrobberies");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(RapesPlusRobberiesMap.class);
        job.setReducerClass(RapesPlusRobberiesReducer.class);


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
        int result = ToolRunner.run(new RapesPlusRobberies(), args);
        System.exit(result);
    }

}

