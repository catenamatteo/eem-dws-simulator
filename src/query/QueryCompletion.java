/*
 * Copyright 2016-2018 Matteo Catena
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package query;

import eu.nicecode.simulator.Agent;
import eu.nicecode.simulator.ComplexEvent;
import eu.nicecode.simulator.Simulator;
import eu.nicecode.simulator.Time;


public class QueryCompletion extends ComplexEvent {

	protected Query query;

	public QueryCompletion(Time time, Agent from, Query query) {
		super(time, from, null);
		this.query = query;
	}

	@Override
	public void execute(Simulator simulator) {

		from.completeRequest(this.query, simulator);
		
	}

}
