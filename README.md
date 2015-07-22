# osnvodsim
an online social network based p2p video-on-demand simulator!


you shold specify the configuration xmls to run:

a sample xml like this:
----------------------------------------------------------------------------
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<system>
		<exec_cycles>2</exec_cycles>
		<exec_time>500</exec_time>
		<seed_type>random</seed_type>
		<seed>1234567890</seed> 
		<random_variation>0.2</random_variation>
	</system>
	<inventory_category>
		<popularity_rank>4</popularity_rank>
		<category>
			<ctg_name>sports</ctg_name>
			<ctg_amount>10</ctg_amount> 
			<ctg_avg_length>50</ctg_avg_length>
		</category>
		<category>
			<ctg_name>games</ctg_name>
			<ctg_amount>10</ctg_amount> 
			<ctg_avg_length>100</ctg_avg_length>
		</category>
	</inventory_category>
	<user_interactivity>
		<uta_avg_incomings_per_second>5</uta_avg_incomings_per_second>
		<uta_watched_fraction>0.8</uta_watched_fraction>
		<uta_probability_watch_next>0.8</uta_probability_watch_next>
		<uta_selecting_same_category>0.7</uta_selecting_same_category>
		<uta_selecting_interested_category>0.8</uta_selecting_interested_category>
	</user_interactivity>
	<distribution>
		<server_config>
			<server>
				<server_bandwidth>500</server_bandwidth>
			</server>
            <server>
                <server_bandwidth>500</server_bandwidth>
            </server>
            <server>
                <server_bandwidth>500</server_bandwidth>
            </server>
            <server>
                <server_bandwidth>500</server_bandwidth>
            </server>
        </server_config>
		<peer_bandwidth>
			<bandwidth>
				<upload_bandwidth>5</upload_bandwidth>
				<download_bandwidth>15</download_bandwidth>
				<bw_fraction>0.07</bw_fraction>
			</bandwidth>
			<bandwidth>
				<upload_bandwidth> 1</upload_bandwidth>
				<download_bandwidth>3</download_bandwidth>
				<bw_fraction>0.79</bw_fraction>
			</bandwidth>
						<bandwidth>
				<upload_bandwidth> 0.5</upload_bandwidth>
				<download_bandwidth>1.5</download_bandwidth>
				<bw_fraction>0.14</bw_fraction>
			</bandwidth>
		</peer_bandwidth>
		<packet_loss_rate>0.1</packet_loss_rate>


		<video_rate>1</video_rate>
        <chunk_size>1</chunk_size>
        <message_size>0.1</message_size>

		<min_latency>0.02</min_latency>
		<max_latency>0.5</max_latency>
		<overlay_type>random</overlay_type>
		<peer_type>pull_based</peer_type>
		<degree>8</degree>
		<packet_lose>0.1</packet_lose>
		<packet_time_out>4</packet_time_out>
		<cache_management_policy>least</cache_management_policy>
		<cache_check_cycle>0.5</cache_check_cycle>
	</distribution>
	<printing_result>
		<output_root_path>\</output_root_path>
		<user_activity_information>user-activity-info.txt</user_activity_information>
		<distribution_information>distribution-info.txt</distribution_information>
		<system_information>system-info.txt</system_information>	
		<statistics>statistics.txt</statistics>
	</printing_result>
</configuration>

----------------------------------------------------------------------------
