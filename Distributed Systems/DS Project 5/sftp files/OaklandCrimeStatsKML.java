// Cannon Wu
// AndrewID: clwu

package edu.cmu.andrew.clwu;

import java.io.IOException;

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
 * MapReduce application that will create a KML for crimes near a pre-determined location.
 */
public class OaklandCrimeStatsKML extends Configured implements Tool {

    /**
     * Subclass that extends Mapper class and objects with types: LongWritable, Text, IntWritable
     */
    public static class OaklandCrimeStatsMap extends Mapper<LongWritable, Text, Text, Text>
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
            // if the 5th column (index 4) equals aggravated assault
            if (lineArray[4].equals("AGGRAVATED ASSAULT")) {
                // Equation for solving whether current radius is less than 200m:
                // (x-a)^2 + (y-b)^2 <= (200 * 3.28084)^2
                double x = Double.parseDouble(lineArray[0]);    // X-coordinate of crime
                double y = Double.parseDouble(lineArray[1]);    // Y-coordinate of crime
                double a = 1354326.897; // X-coordinate of center
                double b = 411447.7828; // Y-coordinate of center
                double distance = 300 * 3.28084; // convert 300m to ft
                // Use pythagorean theorem, if within 300m
                if (Math.pow((x-a),2) + Math.pow((y-b),2) <= Math.pow(distance,2)) {
                    // key: kml stuff, value: coordinates
                    // if coords within 300m, write(first half kml, coords)
                    // Code from: https://en.wikipedia.org/wiki/Keyhole_Markup_Language
                    cell.set("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                            "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n" +
                            "<Document>\n"); // set key to KML file header and document tag
                    StringBuilder sb = new StringBuilder(); // StringBuilder will build placemark string
                    sb.append("<Placemark>\n" +
                            "<name>Crime Location</name>\n" +
                            "<description>Aggravated Assault</description>\n" +
                            "<Point>\n" +
                            "<coordinates>" +
                            lineArray[8] + "," + lineArray[7] +   // add crime-specific coordinates
                            "</coordinates>\n" +
                            "</Point>\n" +
                            "</Placemark>");
                    Text placemark = new Text(sb.toString());
                    context.write(cell,placemark);    // write a constant key (KML header + tags) to ensure 1 mapper only
                                                      // write a variable value which are the crime-specific placemarks
                }
            }
        }
    }

    /**
     * Subclass that extends Reducer class and objects with types: Text, IntWritable
     */
    public static class OaklandCrimeStatsReducer extends Reducer<Text, Text, Text, Text>
    {
        /**
         * Reducer job that will loop through each written value from the Mapper job and append the occurrences
         * It will read something like: [KML Header,[placemarker,placemarker,...]]
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException
        {
            // write (key <whole kml>, iterate thru coords and add to stringbuilder
            // then finally add second-half kml to stringbuilder and write

            StringBuilder sb = new StringBuilder(); // StringBuilder that will build the entire KML file
            sb.append(key); // append the KML file header and document tag
            for(Text value: values)  // for each written placemark (coordinate)
            {
                sb.append(value);   // append each placemark
            }
            sb.append("</Document>\n" +
                    "</kml>");  // append closing tags for KML file

            Text kmlFile = new Text(sb.toString()); // convert SB to Text
            context.write(kmlFile, null);   // write key = entire kml file, null (blank) value
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
        job.setJarByClass(OaklandCrimeStatsKML.class);
        job.setJobName("oaklandcrimestatskml");

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(OaklandCrimeStatsMap.class);
        job.setReducerClass(OaklandCrimeStatsReducer.class);


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
        int result = ToolRunner.run(new OaklandCrimeStatsKML(), args);
        System.exit(result);
    }

}

