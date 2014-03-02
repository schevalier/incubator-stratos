var render = function (theme, data, meta, require) {
    // Re-create the data structure of the cartridges.
    theme('index', {
        body: [
            {
                partial: 'cartridge_info',
                context: {
                    title:'Cartridges',
                    cartridgeInfo:data.cartridgeInfo,
                    lbclusterinfo:data.lbCluster,
                    clusterinfo:data.clusterInfo,
                    host:data.cartridgeInfo.hostName
                }
            }
        ],
        header: [
            {
                partial: 'header',
                context:{
                    title:'Cartridges',
                    my_cartridges:true,
                    button:{
                        link:'/',
                        name:'Back To My Cartridges',
                        class_name:"btn-default",
                        class_icon: 'icon-arrow-left'
                    },
                    has_help:true,
                    help:'Create cartridges like PHP, Python, Ruby etc.. Or create data cartridges with mySql, PostgreSQL. Directly install applications like Drupal, Wordpress etc..'
                }
            }
        ],
        title:[
            {
                partial:'title',
                context:{
                    title:"My Cartridges"
                }
            }
        ]
    });
};