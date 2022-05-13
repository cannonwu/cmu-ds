// Cannon Wu
// AndrewID: clwu
// Code from https://github.com/CMU-Heinz-95702/Project5

// =========== MinTemperatureReducer.java ====================================================
package edu.cmu.andrew.mm6;
import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer; import org.apache.hadoop.mapred.Reporter;

/**
 * This Reducer will call a reduce() method that will check each mapped value against a toy numeric value.
 * This process will allow the program to find the minimum value of the written values for each year.
 */
public class MinTemperatureReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {

    public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text,
            IntWritable> output, Reporter reporter) throws IOException {

        // from the list of values, find the minimum
        int minValue = Integer.MAX_VALUE;   // initialize a minValue to hold a very large value
        while (values.hasNext()) {      // iterate through all mapped values
            minValue = Math.min(minValue, values.next().get()); // set minValue to the lesser of the two values on each iteration
        }
        // emit (key = year, value = minTemp = min for year)
        output.collect(key, new IntWritable(minValue));
    }
}
