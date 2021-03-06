# EJFAT ERSAP data pipeline Example

 
## Application architecture

### `src` directory
Contains Java code for Clas12 data streaming.

### `ejfat` directory
Contains cpp files for the encoder and reassembly engines

#### `engine` subdirectory

Here will go all supporting JANA classes necessary for stream processing.
The local CmakeList.txt will create an .so artifact that will be used during the 
service creation.

#### `services` subproject

The final ERSAP services are just a tiny layer over the `engine` code.
The most important thing to consider is thread-safety of the engine classes.
ERSAP services will run in a multi-thread environment and developers must
ensure thread-safety.

The `service`  CmakeList.txt creates e.g. `libJanaService.so` (or dylib) artifact
that will be used by the ERSAP orchestrator.


## Dependencies

See [ERSAP C++](https://github.com/JeffersonLab/ersap-cpp#build-notes)
for instructions how to build ersap-cpp.

## Build and deploy

### `Java`
./gardlew deploy
Will build and deploy into a proper ERSAP locations
(ERSAP_HOME and ERSAP_USER_DATA environment variable must be set).

### `C++`

Build and deploy the C++ services into `$ERSAP_HOME`:

    > mkdir build
    > cd build
    > cmake ..
    > make
    > make install

## Running the application

### Using the ERSAP Shell

Use the `ersap-shell` to run the services locally (running on a farm will require artifacts 
being accessible on every farm node).
The shell provides a high-level interface to configure and start
the different ERSAP components required to run an application.

1. Start the ERSAP shell:

        >$ERSAP_HOME/bin/ersap-shell

2. Define the application within a `services.yaml` file.
   There is one file ready in the root of the repository. 
   NB: The default location for the application definition file is in $ERSAP_USER_DATA/config dir 

       ersap> set servicesFile services.yaml

3. Optionally you can change the number of parallel threads
   used by the services to process requests

       ersap> set threads <NUM_THREADS>

5. Start the data processing.
   This will start the main Java DPE,
   a C++ DPE if the C++ service is listed in `services.yaml`,
   and it will run the streaming orchestrator to process the data-stream.

       ersap> run local

You can put the above shell commands into a script and run the script instead.
There is a `jana.ersap` script ready in the root of the repository:

      > $ERSAP_HOME/bin/ersap-shell jana.ersap

### Starting the ERSAP DPEs and orchestrator manually

All ERSAP components started by the ERSAP shell can be run manually.

1. Start the main Java DPE:

       > $ERSAP_HOME/bin/j_dpe

2. Start the C++ DPE (if the application uses C++ services):

       > $ERSAP_HOME/bin/c_dpe --fe-host localhost

3. Define the application with a `services.yaml` file.
   There is one file ready in the root of the repository.
   

4. Run the streaming orchestrator with the application configuration file.

       > $ERSAP_HOME/bin/ersap-stream-orchestrator -F -i ./resources -o /tmp/out services.yaml 

   To change the number of parallel threads used by the services to process
   requests, pass the `-t <NUM_THREADS>` option.

