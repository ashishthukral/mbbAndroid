<?php
if($_POST['APP_SECURE_KEY']!='666666'){
	die("AUTH_ERROR: App key authentication failed !!!");	
}
extract($_POST, EXTR_SKIP);

if($_SERVER['HTTP_HOST']=='mbb.comyr.com'){
	$con = mysql_connect("mysql6.000webhost.com","a5739274_ash","root03") or die(mysql_error());
	$db_selected = mysql_select_db("a5739274_ash",$con) or die(mysql_error());
	//echo mysql_error();
}else{
	$con = mysql_connect("127.0.0.1","tbone","tbone") or die(mysql_error());
	$db_selected = mysql_select_db("mbb",$con) or die(mysql_error());
}

if($query_type=="insert_rating"){
	$ins_query="'".mysql_real_escape_string($usability)."','".mysql_real_escape_string($quality)."','".
		mysql_real_escape_string($lookfeel)."'";
	$sql = "INSERT INTO rating(usability,quality,lookfeel) VALUES(".
		$ins_query.")";
	echo "insert query=>".$sql;
	mysql_query($sql,$con);
	$affected_rows=mysql_affected_rows();
	if($affected_rows!=-1){
		echo "*** no of Rows inserted successfully=".$affected_rows.",,, id=".
			mysql_insert_id();
	}else{
		echo "*** insert failed !";
	}
}

if($query_type=="insert"){
	$ins_query="'".mysql_real_escape_string($teacher_id)."','".mysql_real_escape_string($course_id)."','".
		mysql_real_escape_string($info_type)."','".mysql_real_escape_string($info_details)."','".
		mysql_real_escape_string($info_deadline_time)."'";
	$sql = "INSERT INTO mbb_data(teacher_id,course_id,info_type,info_details,info_deadline_time) VALUES(".
		$ins_query.")";
	echo "insert query=>".$sql;
	mysql_query($sql,$con);
	$affected_rows=mysql_affected_rows();
	if($affected_rows!=-1){
		echo "*** no of Rows inserted successfully=".$affected_rows.",,, id=".
			mysql_insert_id();
	}else{
		echo "*** insert failed !";
	}
}

if($query_type=="select"){
	$sql = "SELECT * FROM mbb_data WHERE info_time>'".mysql_real_escape_string($info_time).
		"' AND retired='N' ORDER BY info_time DESC,id DESC";
	echo "select query=>".$sql;
	echo "SELECT_RES_START";
	$result = mysql_query($sql,$con);
	// || - column separator
	while($row = mysql_fetch_array($result)){
		/* get column metadata */
		$i = 0;
		$row_data="";
		while ($i < mysql_num_fields($result)) {
			$meta = mysql_fetch_field($result, $i);
			if ($meta) {
				$row_data=$row_data."$meta->name=".$row[$meta->name]."||";
			}
			$i++;
		}
		echo $row_data;
		echo "ROW_BREAK";	
	}	
	echo "SELECT_RES_STOP";
	mysql_free_result($result);
}


if($query_type=="select_rating"){
	$sql = "SELECT avg(usability),avg(quality),avg(lookfeel) FROM rating";
	$result = mysql_query($sql,$con);
	while($row = mysql_fetch_array($result)){
		/* get column metadata */
		$i = 0;
		$row_data="";
		while ($i < mysql_num_fields($result)) {
			$meta = mysql_fetch_field($result, $i);
			if ($meta) {
				$row_data=$row_data.$row[$meta->name]."||";
			}
			$i++;
		}
		echo $row_data;
	}	
	mysql_free_result($result);
}


function getUpdateFieldQuery($i_update_fields){
	$update_field_query="";
	$token = strtok($i_update_fields,",");
	while ($token != false){
		$update_field_query=$update_field_query.$token."='".
			mysql_real_escape_string($_POST[$token])."',";
		$token = strtok(",");
	}
	$update_field_query=substr($update_field_query,0,-1);
	// echo $update_field_query;
	return $update_field_query;
}



if($query_type=="update"){
	$update_field_query=getUpdateFieldQuery($update_fields);
	$sql = "UPDATE mbb_data SET ".$update_field_query." WHERE id='".
		mysql_real_escape_string($id)."' AND retired='N'";
	echo "update query=>".$sql;
	mysql_query($sql,$con);
	$affected_rows=mysql_affected_rows();
	if($affected_rows!=-1){
		echo "*** no of Rows updated successfully=".$affected_rows;
	}else{
		echo "*** update failed !";
	}
}

if($query_type=="retire"){
	$sql = "UPDATE mbb_data SET retired='Y' WHERE id='".
		mysql_real_escape_string($id)."' AND retired='N'";
	echo "retire query=>".$sql;
	mysql_query($sql,$con);
	$affected_rows=mysql_affected_rows();
	if($affected_rows!=-1){
		echo "*** no of Rows retired successfully=".$affected_rows;
	}else{
		echo "*** retiring failed !";
	}
}

mysql_close($con);

?>
