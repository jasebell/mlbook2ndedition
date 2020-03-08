package mlbook.ch11.imageprocessing.cnn;

import org.datavec.api.io.filters.BalancedPathFilter;
import org.datavec.api.io.labels.ParentPathLabelGenerator;
import org.datavec.api.split.FileSplit;
import org.datavec.api.split.InputSplit;
import org.datavec.image.loader.NativeImageLoader;
import org.datavec.image.recordreader.ImageRecordReader;
import org.datavec.image.transform.FlipImageTransform;
import org.datavec.image.transform.ImageTransform;
import org.datavec.image.transform.WarpImageTransform;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.*;
import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.*;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.util.ModelSerializer;

import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.dataset.api.preprocessor.DataNormalization;
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BasicCNN {
    protected static final Logger log = LoggerFactory.getLogger(BasicCNN.class);

    public static final String pathToImages = "/Users/jasebell/bookwork/mlbook2ndedition/data/ch11/cnn/101_ObjectCategories";

    public static void main(String[] args) throws Exception {
        int imageHeight = 200;
        int imageWidth = 300;
        int channels = 3;

        int seed = 123;
        Random randNumGen = new Random(seed);

        int batchSize = 50;
        int numOutputClasses = 101;
        int epoch = 10;

        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes", "0");
        System.setProperty("org.bytedeco.javacpp.maxbytes", "0");

        File trainingData = new File(pathToImages);
        FileSplit trainingDataSplit = new FileSplit(trainingData, NativeImageLoader.ALLOWED_FORMATS, randNumGen);
        ParentPathLabelGenerator labelMaker = new ParentPathLabelGenerator();

        BalancedPathFilter pathFilter = new BalancedPathFilter(randNumGen, labelMaker,0, numOutputClasses, batchSize);
        InputSplit[] filesInDirSplit = trainingDataSplit.sample(pathFilter, 80, 20); //Splitting the test and train data

        InputSplit trainingSet = filesInDirSplit[0];
        InputSplit testingSet = filesInDirSplit[1];

        ImageTransform transform1 = new FlipImageTransform(randNumGen);
        ImageTransform transform2 = new FlipImageTransform(new Random(seed));
        ImageTransform warptranform = new WarpImageTransform(randNumGen,42);
        List<ImageTransform> tranforms = Arrays.asList(new ImageTransform[] {transform1, warptranform, transform2});


        ImageRecordReader recordReader = new ImageRecordReader(imageHeight, imageWidth, channels, labelMaker);
        recordReader.initialize(trainingSet);

        DataNormalization scaler = new ImagePreProcessingScaler(0, 1);
        DataSetIterator dataSetIterator;

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder().trainingWorkspaceMode(WorkspaceMode.SEPARATE)
                .inferenceWorkspaceMode(WorkspaceMode.SINGLE)
                .seed(seed)
                .iterations(1)
                .activation(Activation.IDENTITY).weightInit(WeightInit.XAVIER)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .learningRate(.006)
                .updater(Updater.NESTEROVS)
                .regularization(true).l2(.0001)
                .convolutionMode(ConvolutionMode.Same).list()
                // block 1
                .layer(0, new ConvolutionLayer.Builder(new int[] {5, 5}).name("image_array").stride(new int[]{1, 1})
                        .nIn(3)
                        .nOut(16).build())
                .layer(1, new BatchNormalization.Builder().build())
                .layer(2, new ActivationLayer.Builder().activation(Activation.RELU).build())
                .layer(3, new ConvolutionLayer.Builder(new int[] {5, 5}).stride(new int[]{1, 1}).nIn(16).nOut(16)
                        .build())
                .layer(4, new BatchNormalization.Builder().build())
                .layer(5, new ActivationLayer.Builder().activation(Activation.RELU).build())
                .layer(6, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                        new int[] {2, 2}).build())
                .layer(7, new ConvolutionLayer.Builder(new int[] {5, 5}).stride(new int[]{2, 2}).nIn(16).nOut(16)
                        .build())
                .layer(8, new BatchNormalization.Builder().build())
                .layer(9, new ActivationLayer.Builder().activation(Activation.RELU).build())
                .layer(10, new SubsamplingLayer.Builder(SubsamplingLayer.PoolingType.AVG,
                        new int[] {2, 2}).build())
                .layer(11, new DropoutLayer.Builder(0.5).build())
                .layer(12, new DenseLayer.Builder().name("ffn2").nOut(256).build())
                .layer(13, new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .name("output").nOut(numOutputClasses).activation(Activation.SOFTMAX).build())
                .setInputType(InputType.convolutional(imageHeight, imageWidth, channels))
                .backprop(true)
                .pretrain(false)
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();

        model.setListeners(new ScoreIterationListener(10));

        log.info("Training the model....");
        for (ImageTransform transform: tranforms) {

            log.info("Training using image transform: s" + transform.getClass().toString());
            recordReader.initialize(trainingSet,transform);
            dataSetIterator = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numOutputClasses);
            scaler.fit(dataSetIterator);
            dataSetIterator.setPreProcessor(scaler);
            for (int j = 0; j < epoch; j++) {
                model.fit(dataSetIterator);
            }
        }
        recordReader.reset();
        //Training our data on the original images
        recordReader.initialize(trainingSet);
        dataSetIterator = new RecordReaderDataSetIterator(recordReader, batchSize, 1, numOutputClasses);
        scaler.fit(dataSetIterator);
        dataSetIterator.setPreProcessor(scaler);
        for (int j = 0; j < epoch; j++) {

            model.fit(dataSetIterator);
        }
        log.info("*****Testing the model ********");

        recordReader.reset();

        recordReader.initialize(testingSet);
        DataSetIterator testIter = new RecordReaderDataSetIterator(recordReader,batchSize,1,numOutputClasses);
        scaler.fit(testIter);
        testIter.setPreProcessor(scaler);

        Evaluation eval = new Evaluation(numOutputClasses);

        while(testIter.hasNext()){
            DataSet next = testIter.next();
            INDArray output = model.output(next.getFeatureMatrix());
            eval.eval(next.getLabels(),output);

        }

        log.info(eval.stats()); //Printing the accuracy and precision of the model.

        //Below command is to save our model at the local directory excluding the weights.
        File locationToSave = new File(System.getProperty("user.dir"),"mycnntest.zip");
        ModelSerializer.writeModel(model,locationToSave,false);
    }

}
