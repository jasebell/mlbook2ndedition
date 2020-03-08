package mlbook.ch09.ann.dl4j;

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

public class BasicMLP {

    private static Logger log = LoggerFactory.getLogger(BasicMLP.class);


    public static void main(String[] args) throws  Exception {
        int numLinesToSkip = 1;
        String delimiter = ",";
        RecordReader recordReader = new CSVRecordReader(numLinesToSkip,delimiter);

        recordReader.initialize(new FileSplit(new File("/path/to/data/ch09/dl4j/")));

        int labelIndex = 3;
        int numClasses = 4;
        int batchSize = 100000;
        double evalsplit = 0.65;

        DataSetIterator dataSetIterator = new RecordReaderDataSetIterator(recordReader,batchSize,labelIndex,numClasses);

        DataSet allData = dataSetIterator.next();
        allData.shuffle();
        SplitTestAndTrain testAndTrainingData = allData.splitTestAndTrain(evalsplit);  //Use 65% of data for training

        DataSet trainingData = testAndTrainingData.getTrain();
        DataSet testData = testAndTrainingData.getTest();

        DataNormalization dataNormalizer = new NormalizerStandardize();
        dataNormalizer.fit(trainingData);
        dataNormalizer.transform(trainingData);
        dataNormalizer.transform(testData);


        final int numInputs = 3;
        final int hiddenNodes = 4;
        int outputNum = 4;
        int iterations = 2000;
        long seed = 6;


        log.info("Building model....");
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


        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(100));
        model.fit(trainingData);

        Evaluation eval = new Evaluation(4);
        log.info("Getting evaluation");
        INDArray output = model.output(testData.getFeatureMatrix());
        log.info("Getting evaluation output");
        eval.eval(testData.getLabels(), output);
        System.out.println(eval.stats());

        File locationToSave = new File("/path/to/models/basicmlpmodel.zip");
        boolean saveUpdater = false;
        ModelSerializer.writeModel(model, locationToSave, saveUpdater);
    }
}

