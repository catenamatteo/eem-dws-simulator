package engine.mmk;

import java.util.concurrent.TimeUnit;

import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;
import query.Query;
import util.Clock;

@Deprecated
public class QueryMatcherClock extends Clock {

	private QueryMatcher matcher;

	QueryMatcherClock(Time start, QueryMatcher matcher) {
		super(start, new Time(1000, TimeUnit.MILLISECONDS));
		this.matcher = matcher;
	}

	@Override
	protected boolean doClock(Simulator simulator) {

		Query nextQuery = ((ShardServer) matcher.getShardServer()).getNextQuery();
		if (nextQuery != null) {
			
			matcher.serveQuery(nextQuery, simulator);
			return false;
		}
		return true;
		
	}

}
