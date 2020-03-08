package mlbook.ch6.clustering;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.util.Random;

public class WekaCluster {

	public WekaCluster(String filepath) {
		try {
			Instances data = DataSource.read(filepath);

			int clusters = calculateRuleOfThumb(data.numInstances());
			System.out.println("Creating k-means model with " + clusters + " clusters.");
			SimpleKMeans kMeans = new SimpleKMeans();
			kMeans.setNumClusters(clusters);
			kMeans.buildClusterer(data);
			
			showCentroids(kMeans);
			showInstanceInCluster(kMeans, data);

			testRandomInstances(kMeans);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public int calculateRuleOfThumb(int rows) {
		return (int)Math.sqrt(rows/2);
	}

	public void showCentroids(SimpleKMeans kMeans) {
		 Instances centroids = kMeans.getClusterCentroids();
		 for(int i = 0; i < centroids.numInstances(); i++) {
		 	System.out.println("Centroid: " + i + ": " + centroids.instance(i));
		 }
	}

	public void showInstanceInCluster(SimpleKMeans kMeans, Instances data) {
		try {
			for(int i = 0; i < data.numInstances(); i++) {
				System.out.println("Instance " + i + " is in cluster " + kMeans.clusterInstance(data.instance(i)));
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int predictCluster(SimpleKMeans kMeans, double x, double y) {
		int clusterNumber = -1;
		try {
			double[] newdata = new double[] {x,y};
			Instance testInstance = new Instance(1.0, newdata);
			clusterNumber = kMeans.clusterInstance(testInstance);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return clusterNumber;
	}

	public void testRandomInstances(SimpleKMeans kMeans) {
		Random rand = new Random();
		for(int i = 0; i < 100; i++) {
			double x = rand.nextInt(200);
			double y = rand.nextInt(200);
			System.out.println(x + "/" + y + " test in cluster " + predictCluster(kMeans, x, y));
		}
	}

	public static void main(String[] args) {
		// Pass the arff location and the number of clusters we want
		WekaCluster wc = new WekaCluster("/Users/jasebell/bookwork/kmeansdata.arff");

	}

}
