/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

import joketeller.JokeTeller;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "myJokeApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend",
                ownerName = "backend",
                packagePath = ""
        )
)
public class MyEndpoint {

    /**
     * A simple endpoint method that takes a name and says Hi back
     */

    /**
     * this optional annotation is used to supply different defaults
     * than the ones provided by the @Api and @ApiClass annotations
     *
     * @param name specifies the name of the method.  If not specified
     *             the API defaults to "myapi'
     *
     */
    @ApiMethod(name = "sayHi")
    /**
     * @Named is required for non-entity type paremeters passed to the
     * server-side methods.
     *
     *
     */
    public MyBean sayHi(@Named("name") String name) {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }

    @ApiMethod(name = "getJoke")
    public MyBean getJoke(){
        MyBean joke = new MyBean();
        joke.setData(new JokeTeller().getJoke());
        return joke;
    }


}
