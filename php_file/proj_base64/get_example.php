<?php

if( isset($_GET['id']) ){

    $id = $_GET['id'];
    $msg = "Hey Welcome Mr. John Doe. Your id is ".$id;

    $array['data'] = $msg;

    echo json_encode( $array );

}


?>