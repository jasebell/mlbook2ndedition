package mlbook.ch12.kafka.mlp;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.SplitTestAndTrain;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.NormalizerStandardize;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

/**
 * @author Jase Bell
 */
public class ANNBuilder {

    private static Logger log = LoggerFactory.getLogger(ANNBuilder.class);



    private static long getHiddenNodeEstimate(int inputs, int outputs, int samplesize) {
        Random r = new Random();
        double out  = (samplesize / ((inputs + outputs) * r.nextInt(9) + 1));
        return Math.round(out);
    }



    public static void main(String[] args) throws  Exception {
    // Everything is classed as a new run so we want a UUID for each model run.

        String uuid = UUID.randomUUID().toString();




    long start = System.currentTimeMillis();
        //First: get the dataset using the record reader. CSVRecordReader handles loading/parsing
        int numLinesToSkip = 0;
        String delimiter = ",";
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);

        recordReader.initialize(new FileSplit(new File("/opt/mlbook/testdata/")));


        //Second: the RecordReaderDataSetIterator handles conversion to DataSet objects, ready for use in neural network
        int labelIndex = 3;     //4 values in each row of the CSV: 3 input features followed by an integer label (class) index.
        int numClasses = 11;     //11 classes
        int batchSize = 474;
        double evalsplit = 0.65;

        DataSetIterator iterator = new RecordReaderDataSetIterator(recordReader,batchSize,labelIndex,numClasses);

        DataSet allData = iterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrain = allData.splitTestAndTrain(evalsplit);  //Use 65% of data for training

        DataSet trainingData = testAndTrain.getTrain();
        DataSet testData = testAndTrain.getTest();

        //We need to normalize our data. We'll use NormalizeStandardize (which gives us mean 0, unit variance):
        DataNormalization normalizer = new NormalizerStandardize();
        normalizer.fit(trainingData);
        normalizer.transform(trainingData);
        normalizer.transform(testData);


        final int numInputs = 3;
        int outputNum = 11;
        int iterations = 2000;
        long seed = 6;

        int hiddenNodes = (int)getHiddenNodeEstimate(numInputs, outputNum, batchSize);



        log.info("Build model....");
        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
            .seed(seed)
            .iterations(iterations)
            .activation(Activation.TANH)
            .weightInit(WeightInit.XAVIER)
            .learningRate(0.1)
            .regularization(true).l2(1e-4)
            .list()
            .layer(0, new DenseLayer.Builder().nIn(numInputs).nOut(hiddenNodes).build())
            .layer(1, new DenseLayer.Builder().nIn(hiddenNodes).nOut(hiddenNodes).build())
            .layer(2, new DenseLayer.Builder().nIn(hiddenNodes).nOut(hiddenNodes).build())
                .layer(3, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .activation(Activation.SOFTMAX)
                .nIn(hiddenNodes).nOut(outputNum).build())
            .backprop(true).pretrain(false)
            .build();

        //run the model
        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));

        model.fit(trainingData);

        log.info("Made it here.....");
        long stop = System.currentTimeMillis();
        long timetaken = stop - start;


        System.out.println("Took " + timetaken + " millis");

        //evaluate the model on the test set
        Evaluation eval = new Evaluation(11);
        log.info("Getting evaluation");
        INDArray output = model.output(testData.getFeatureMatrix());
        log.info("Getting evaluation output");
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());

        // Write output results to database
        DBTools.writeResultsToDB(uuid, evalsplit, timetaken, eval.f1() , eval.stats(), "mlp");

        // Save model
        File locationToSave = new File("/opt/mlbook/testdata/models/" + uuid + ".zip");
        boolean saveUpdater = false;
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);

    }

}
