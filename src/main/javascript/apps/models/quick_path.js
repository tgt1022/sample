steal('jquery/model', function() {
  /**
   * @class Apps.Models.QuickPath
   * @parent index
   * @inherits jQuery.Model
   * Wraps backend quick_path services.  
   */
  $.Model('Apps.Models.QuickPath',
    /* @Static */
    {
      findAll: "/service/quick_paths.json",
      findOne : "/service/quick_paths/{id}.json",
      create : "/service/quick_paths.json",
      update : "/service/quick_paths/{id}.json",
      destroy : "/service/quick_paths/{id}.json"
    },
    /* @Prototype */
    { });
});