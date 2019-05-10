package cn.linkedcare.springboot.hbase2es.observer;

import java.util.Optional;

import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;

public class Hbase2EsObserver  implements RegionObserver,RegionCoprocessor{
	public void init(){
		
	}
	
	@Override
	public Optional<RegionObserver> getRegionObserver() {
		// Extremely important to be sure that the coprocessor is invoked as a RegionObserver
		return Optional.of(this);
	
		
	}
	
}
