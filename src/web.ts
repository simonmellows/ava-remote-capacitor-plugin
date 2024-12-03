import { WebPlugin } from '@capacitor/core';

import type { AvaRemotePluginPlugin } from './definitions';

export class AvaRemotePluginWeb extends WebPlugin implements AvaRemotePluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }

  /*async initializePlugin(): Promise<void>{
    console.log('Initializing AVA plugin...');
  }*/

  async sendConfig(options: {config: Object}): Promise<void>{
    console.log('Sending config: ', options);
  }
}
