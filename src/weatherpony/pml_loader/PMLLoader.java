package weatherpony.pml_loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URLClassLoader;
import java.util.concurrent.Callable;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import joptsimple.OptionParser;
import joptsimple.OptionSet;
import joptsimple.OptionSpec;
import weatherpony.pml.launch.PMLLoadFocuser;
import weatherpony.pml.launch.PMLRoot;

import com.google.common.base.Throwables;

public class PMLLoader implements Callable<Callable<Callable<Void>>>{
	static{
		PMLLoadFocuser.addFocusedLoadNote_late(PMLLoader.class);
	}
	public PMLLoader(){
		instance = this;
		BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("PMLLoad.txt")));
		try{
			String line = reader.readLine();
			File load = new File(PMLRoot.selfLoadPrefix,"PML");
			load = new File(load, "installs");
			load = new File(load,line);
			ZipFile loadjar = new JarFile(load);
			ZipEntry loadfile = loadjar.getEntry("PMLLoadClass.txt");
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(loadjar.getInputStream(loadfile)));
			String loadclass = reader2.readLine();
			PMLRoot.addURL((URLClassLoader)Thread.currentThread().getContextClassLoader(), load.toURI().toURL());
			loadjar.close();
			loadedPML = (Callable<Callable>) Class.forName(loadclass, true, Thread.currentThread().getContextClassLoader()).newInstance();
			System.out.println("PMLLoader found core");
		}catch(Throwable e){
			e.printStackTrace();
			throw Throwables.propagate(e);
		}
	}
	public static PMLLoader instance;
	private Callable loadedPML;
	@Override
	public Callable call() throws Exception{
		System.out.println("PMLLoader fetching loaded core");
		return loadedPML;
	}
}
