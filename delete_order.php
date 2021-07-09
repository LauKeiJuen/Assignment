<?php

	$DATABASE_HOST = 'localhost';
	$DATABASE_USER = 'root';
	$DATABASE_PASS = '';
	$DATABASE_NAME = 'rms';

	// Try and connect using the info above.
	$con = mysqli_connect($DATABASE_HOST, $DATABASE_USER, $DATABASE_PASS, $DATABASE_NAME);
	if ( mysqli_connect_errno() ) {
		// If there is an error with the connection, stop the script and display the error.
		exit('Failed to connect to MySQL: ' . mysqli_connect_error());
	}
	
	$order_item_id = $_GET["id"];
	
	$query = "DELETE FROM order_item_table WHERE order_item_id = '$order_item_id'";
	
	if(mysqli_query($con, $query)){
		mysqli_close($con);
		echo '<script>alert("Order deleted.");';
		echo'window.location.href = "order.php";</script>';
		exit;
	} else {
		echo "ERROR Deleting: ". mysqli_error();
	}
?>